package net.javaguides.ems.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.security.AuthAccount;
import net.javaguides.ems.security.AuthAccountService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NullMarked
@Profile("auth")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final AuthAccountService authAccountService;

  @Override
  public UserDetails loadUserByUsername(@NonNull String email) {
    AuthAccount account = authAccountService.findByEmail(email)
        .filter(candidate -> candidate.password() != null)
        .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + email));

    return org.springframework.security.core.userdetails.User.builder()
        .username(account.email())
        .password(account.password())
        .authorities(List.of(new SimpleGrantedAuthority(account.role())))
        .build();
  }
}
