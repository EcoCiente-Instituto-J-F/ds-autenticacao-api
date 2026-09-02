package br.com.ecociente.autenticacao.config.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.ecociente.autenticacao.core.gateway.UsuarioGateway;
import br.com.ecociente.autenticacao.core.service.UsuarioPerfilService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

  private final UsuarioGateway usuarioGateway;
  private final UsuarioPerfilService usuarioPerfilService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var usuario = usuarioGateway.buscarPorEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
    var perfil = usuarioPerfilService.perfil(usuario);

    return User.builder()
        .username(usuario.getEmail())
        .password(usuario.getSenhaHash())
        .disabled(Boolean.FALSE.equals(usuario.getAtivo()))
        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name())))
        .build();
  }
}
