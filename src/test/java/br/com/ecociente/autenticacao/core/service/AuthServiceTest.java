package br.com.ecociente.autenticacao.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecociente.autenticacao.core.domain.Autenticacao;
import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.SessaoAutenticada;
import br.com.ecociente.autenticacao.core.domain.Usuario;
import br.com.ecociente.autenticacao.core.domain.UsuarioCredenciais;
import br.com.ecociente.autenticacao.core.gateway.AutenticacaoGateway;
import br.com.ecociente.autenticacao.core.gateway.AutenticadorCredenciaisPort;
import br.com.ecociente.autenticacao.core.gateway.TokenProvaiderPort;
import br.com.ecociente.autenticacao.core.gateway.UsuarioGateway;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private AutenticadorCredenciaisPort autenticadorCredenciaisPort;

  @Mock
  private TokenProvaiderPort tokenProvaiderPort;

  @Mock
  private UsuarioPerfilService usuarioPerfilService;

  @Mock
  private UsuarioGateway usuarioGateway;

  @Mock
  private AutenticacaoGateway autenticacaoGateway;

  @InjectMocks
  private AuthService authService;

  @Test 
  @DisplayName("Deve autenticar com sucesso e retornar sessão autenticada")
  void shouldAutenticateSucessful(){
    var credenciais = new UsuarioCredenciais("test@email.com","@sEnha12345678");
    var usuario = Usuario.builder()
        .id(1)
        .nome("Test")
        .email(credenciais.getEmail())
        .ativo(true)
        .tipoUsuario("morador")
        .build();

    doNothing().when(autenticadorCredenciaisPort).autenticar(credenciais.getEmail(),credenciais.getSenha());
    when(usuarioGateway.buscarPorEmail(credenciais.getEmail())).thenReturn(Optional.of(usuario));
    when(usuarioPerfilService.perfil(usuario)).thenReturn(PerfilUsuarioType.MORADOR);
    when(tokenProvaiderPort.gerarToken(usuario, PerfilUsuarioType.MORADOR)).thenReturn("mocked.jwt.token");
    when(tokenProvaiderPort.getExpiracaoSegundos()).thenReturn(3600L);

    SessaoAutenticada resultado = authService.login(credenciais);

    assertNotNull(resultado);
    assertEquals("mocked.jwt.token", resultado.getToken());
    assertEquals(1, resultado.getUsuarioId());
    assertEquals(PerfilUsuarioType.MORADOR, resultado.getPerfil());
    verify(autenticacaoGateway,times(1)).salvar(any(Autenticacao.class));
  }

}  