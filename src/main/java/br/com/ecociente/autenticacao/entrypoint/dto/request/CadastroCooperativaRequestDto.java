package br.com.ecociente.autenticacao.entrypoint.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroCooperativaRequestDto(
  @NotBlank String nomeResponsavel,
  @Email @NotBlank String email,
  @NotBlank @Size(min = 8) String senha,
  @NotBlank String confirmarSenha,
  @NotBlank @Size(min = 14, max = 14) String cnpj,
  String telefone,
  @NotBlank @Size(min = 8, max = 8) String cep,
  @NotBlank @Size(min = 2, max = 2) String estado,
  @NotBlank String cidade,
  String bairro,
  @NotBlank String rua,
  @NotBlank String numero,
  String complemento
) {
}
