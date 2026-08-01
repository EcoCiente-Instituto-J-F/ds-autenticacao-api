package br.com.ecociente.autenticacao.entrypoint.dto.request;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequestDto(
  @NotBlank String nome,
  @Email @NotBlank String email,
  @NotBlank @Size(min = 8) String senha,
  @NotBlank String confirmarSenha,
  @NotNull PerfilUsuarioType perfil,
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
