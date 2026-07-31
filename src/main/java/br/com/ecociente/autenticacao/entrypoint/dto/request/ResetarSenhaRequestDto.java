package br.com.ecociente.autenticacao.entrypoint.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetarSenhaRequestDto(
  @Email @NotBlank String email,
  @NotBlank String codigoRecuperacao,
  @NotBlank @Size(min = 8) String novaSenha,
  @NotBlank String confirmarNovaSenha
) {
}
