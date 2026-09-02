package br.com.ecociente.autenticacao.core.domain;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class Autenticacao {
  Integer id;
  Integer usuarioId;
  String token;
  String tipoToken;
  OffsetDateTime criadoEm;
  Integer expiradoEm;
  // PerfilUsuarioType perfil;
  // String nome;
  // String email;
}
