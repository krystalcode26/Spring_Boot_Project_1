package net.javaguides.ems.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.AuthUserResponse;
import net.javaguides.ems.dto.LoginRequest;
import net.javaguides.ems.dto.TokenResponse;
import net.javaguides.ems.security.AuthAccount;
import net.javaguides.ems.security.AuthAccountService;
import net.javaguides.ems.security.JwtProperties;
import net.javaguides.ems.security.JwtService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Profile("auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final AuthAccountService authAccountService;
  private final JwtService jwtService;
  private final JwtProperties jwtProperties;

  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    AuthAccount account = authAccountService.findByEmail(request.getUsername())
        .orElseThrow(() -> new IllegalStateException("Authenticated account not found"));
    return new TokenResponse(
        jwtService.generateToken(account),
        "Bearer",
        jwtProperties.getExpirationMs() / 1000);
  }

  @GetMapping("/me")
  public AuthUserResponse me(@AuthenticationPrincipal Jwt jwt) {
    List<String> roles = JwtService.rolesFromClaim(jwt.getClaim("roles")).stream()
        .map(authority -> {
          String role = authority.getAuthority();
          return role != null ? role.replace("ROLE_", "") : "";
        })
        .toList();
    return new AuthUserResponse(
        jwt.getSubject(),
        jwt.getClaimAsString("email"),
        jwt.getClaimAsString("name"),
        roles);
  }
}
