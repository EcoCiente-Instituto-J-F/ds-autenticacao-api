package br.com.ecociente.autenticacao.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class SessaoAutenticada {
  String token;
  String tipoToken;
  Integer expiraEm;
  Integer usuarioId;
  String nome;
  String email;
  PerfilUsuarioType perfil;
 
}
