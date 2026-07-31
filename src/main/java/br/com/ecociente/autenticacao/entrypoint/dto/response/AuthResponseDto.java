package br.com.ecociente.autenticacao.entrypoint.dto.response;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;

public record AuthResponseDto(
  String token,
  String tipoToken,
  Long expiraEmSegundos,
  Integer usuarioId,
  String nome,
  String email,
  PerfilUsuarioType perfil
) {
}
