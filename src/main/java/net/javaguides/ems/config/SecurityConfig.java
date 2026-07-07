package net.javaguides.ems.config;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.security.JsonAccessDeniedHandler;
import net.javaguides.ems.security.JsonAuthenticationEntryPoint;
import net.javaguides.ems.security.JwtAuthenticationFilter;
import net.javaguides.ems.security.OAuth2LoginSuccessHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final JsonAuthenticationEntryPoint authenticationEntryPoint;
  private final JsonAccessDeniedHandler accessDeniedHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .requestMatchers("/oauth2/**", "/login/**").permitAll()
            .requestMatchers("/name/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/log/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated())
        .oauth2Login(oauth -> oauth.successHandler(oAuth2LoginSuccessHandler))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler));
    return http.build();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
    try {
      return configuration.getAuthenticationManager();
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to configure AuthenticationManager", ex);
    }
  }
}
