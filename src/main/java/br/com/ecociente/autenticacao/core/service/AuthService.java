package br.com.ecociente.autenticacao.core.service;

import org.springframework.stereotype.Service;

import br.com.ecociente.autenticacao.core.domain.Autenticacao;
import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.SessaoAutenticada;
import br.com.ecociente.autenticacao.core.domain.Usuario;
import br.com.ecociente.autenticacao.core.domain.UsuarioCredenciais;
import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.gateway.AutenticacaoGateway;
import br.com.ecociente.autenticacao.core.gateway.AutenticadorCredenciaisPort;
import br.com.ecociente.autenticacao.core.gateway.TokenProvaiderPort;
import br.com.ecociente.autenticacao.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AutenticadorCredenciaisPort autenticadorCredenciaisPort;
  private final TokenProvaiderPort tokenProvaiderPort;
  private final UsuarioPerfilService usuarioPerfilService;
  private final UsuarioGateway usuarioGateway;
  private final AutenticacaoGateway autenticacaoGateway;
  
  public SessaoAutenticada login(UsuarioCredenciais credenciais){
    autenticadorCredenciaisPort.autenticar(credenciais.getEmail(),credenciais.getSenha());

      Usuario usuario = usuarioGateway.buscarPorEmail(credenciais.getEmail())
      .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      String token = tokenProvaiderPort.gerarToken(usuario, perfil);
      int expiracaoSegundos = Math.toIntExact(tokenProvaiderPort.getExpiracaoSegundos());
      autenticacaoGateway.salvar(new Autenticacao(
        null,
        usuario.getId(),
        token,
        "bearer",
        null,
        expiracaoSegundos
      ));

      return new SessaoAutenticada(
        token,
        "Bearer",
        expiracaoSegundos,
        usuario.getId(),
        usuario.getNome(),
        usuario.getEmail(),
        perfil);
  }
  
}
