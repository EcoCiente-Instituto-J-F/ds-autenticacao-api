package br.com.ecociente.autenticacao.core.gateway;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.Usuario;

public interface TokenProvaiderPort {
  String gerarToken(Usuario usuario, PerfilUsuarioType perfil);
  Long getExpiracaoSegundos();
}
