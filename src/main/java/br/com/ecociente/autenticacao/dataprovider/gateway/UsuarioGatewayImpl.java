package br.com.ecociente.autenticacao.dataprovider.gateway;

import br.com.ecociente.autenticacao.dataprovider.repository.UsuarioRepository;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecociente.autenticacao.core.domain.Usuario;
import br.com.ecociente.autenticacao.core.gateway.UsuarioGateway;
import br.com.ecociente.autenticacao.dataprovider.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class UsuarioGatewayImpl implements UsuarioGateway{
  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper mapper;

  @Override
  public Optional<Usuario> buscarPorEmail(String email) {
    return usuarioRepository.findByEmailIgnoreCase(email).map(mapper::toDomain);
  }

  @Override
  public boolean existePorEmail(String email) {
    return usuarioRepository.existsByEmailIgnoreCase(email);
  }
  
}
