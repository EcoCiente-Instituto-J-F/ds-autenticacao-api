package br.com.ecociente.autenticacao.core.gateway;

import java.util.Optional;

import br.com.ecociente.autenticacao.core.domain.Usuario;

public interface UsuarioGateway {
  Optional<Usuario> buscarPorEmail(String email);
  boolean existePorEmail(String email);
}
