package br.com.ecociente.autenticacao.config.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.dataprovider.entity.UsuarioEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final Long expirationMs;

  public JwtService(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.expiration-ms}") Long expirationMs) {

    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String gerarToken(UsuarioEntity usuario, PerfilUsuarioType perfil) {
    Instant agora = Instant.now();
    Instant expiracao = agora.plusMillis(expirationMs);

    return Jwts.builder()
        .subject(usuario.getEmail())
        .claims(Map.of(
            "usuarioId", usuario.getId(),
            "nome", usuario.getNome(),
            "perfil", perfil.name()))
        .issuedAt(Date.from(agora))
        .expiration(Date.from(expiracao))
        .signWith(secretKey)
        .compact();
  }

  public String extrairEmail(String token) {
    return extrairClaims(token).getSubject();
  }

  public boolean isTokenValido(String token, String email) {
    Claims claims = extrairClaims(token);

    return claims.getSubject().equalsIgnoreCase(email)
        && claims.getExpiration().after(new Date());
  }

  public Long getExpirationSeconds() {
    return expirationMs / 1000;
  }

  private Claims extrairClaims(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}