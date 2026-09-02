package br.com.ecociente.autenticacao.core.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import br.com.ecociente.autenticacao.config.security.JwtService;
import br.com.ecociente.autenticacao.core.domain.Autenticacao;
import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.SessaoAutenticada;
import br.com.ecociente.autenticacao.core.domain.Usuario;
import br.com.ecociente.autenticacao.core.domain.UsuarioCredenciais;
import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.gateway.AutenticacaoGateway;
import br.com.ecociente.autenticacao.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UsuarioPerfilService usuarioPerfilService;
  private final UsuarioGateway usuarioGateway;
  private final AutenticacaoGateway autenticacaoGateway;
  
  public SessaoAutenticada login(UsuarioCredenciais credenciais){
    authenticationManager.authenticate( 
      new UsernamePasswordAuthenticationToken(credenciais.getEmail(), credenciais.getSenha()));

      Usuario usuario = usuarioGateway.buscarPorEmail(credenciais.getEmail())
      .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      String token = jwtService.gerarToken(usuario, perfil);
      autenticacaoGateway.salvar(new Autenticacao(
        null,
        usuario.getId(),
        token,
        "bearer",
        null,
        Math.toIntExact(jwtService.getExpirationSeconds())
      ));

      return new SessaoAutenticada(
        token,
        "Bearer",
        jwtService.getExpirationSeconds().intValue(),
        usuario.getId(),
        usuario.getNome(),
        usuario.getEmail(),
        perfil);
  }
  
}
