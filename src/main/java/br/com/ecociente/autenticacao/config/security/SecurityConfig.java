package br.com.ecociente.autenticacao.config.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UsuarioDetailsService usuarioDetailsService;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
          .requestMatchers(
              "/auth/**",
              "/swagger-ui/**",
              "/v3/api-docs/**",
              "/actuator/health")
          .permitAll()
          .anyRequest().authenticated())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new PasswordEncoder() {
      @Override
      public String encode(CharSequence rawPassword) {
        return hashSHA256(rawPassword.toString());
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
          return false;
        }
        
        return hashSHA256(rawPassword.toString()).equalsIgnoreCase(encodedPassword);
      }

      private String hashSHA256(String input) {
        try {
          MessageDigest digest = MessageDigest.getInstance("SHA-256");
          byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
          StringBuilder hexString = new StringBuilder();
          for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
              hexString.append('0');
            }
            hexString.append(hex);
          }
          return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
          throw new RuntimeException("Erro ao calcular hash SHA-256 da senha", e);
        }
      }
    };
  }
}
