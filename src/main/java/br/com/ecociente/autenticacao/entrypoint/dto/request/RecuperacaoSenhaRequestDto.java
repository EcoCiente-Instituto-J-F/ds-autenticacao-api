package br.com.ecociente.autenticacao.entrypoint.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperacaoSenhaRequestDto(
  @Email @NotBlank String email
) {
}
