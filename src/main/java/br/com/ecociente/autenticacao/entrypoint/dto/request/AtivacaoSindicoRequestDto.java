package br.com.ecociente.autenticacao.entrypoint.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtivacaoSindicoRequestDto(
  @Email @NotBlank String email,
  @NotBlank @Size(min = 8) String senha,
  @NotBlank String confirmarSenha
) {
}
