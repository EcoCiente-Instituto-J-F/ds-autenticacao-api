package br.com.ecociente.autenticacao.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import br.com.ecociente.autenticacao.config.security.JwtService;
import br.com.ecociente.autenticacao.core.domain.Autenticacao;
import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.SessaoAutenticada;
import br.com.ecociente.autenticacao.core.domain.Usuario;
import br.com.ecociente.autenticacao.core.domain.UsuarioCredenciais;
import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.gateway.AutenticacaoGateway;
import br.com.ecociente.autenticacao.core.gateway.UsuarioGateway;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private UsuarioPerfilService usuarioPerfilService;

  @Mock
  private UsuarioGateway usuarioGateway;

  @Mock
  private AutenticacaoGateway autenticacaoGateway;

  @InjectMocks
  private AuthService authService;

  private Usuario usuario;
  private UsuarioCredenciais credenciais;

  @BeforeEach
  void setUp() {
    usuario = new Usuario(
        1,
        "Emanuelly Mendes",
        "emanuelly@gmail.com",
        "$2a$10$hash",
        true,
        "morador");

    credenciais = new UsuarioCredenciais(
        "emanuelly@gmail.com",
        "Senha@T3ste");
  }

  @Nested
  @DisplayName("login")
  class Login {

    @Test
    @DisplayName("Deve autenticar e retornar sessão com token quando credenciais válidas")
    void shouldAuthenticateAndReturnSessionWhenCredentialsValid() {
      Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenReturn(authentication);
      when(usuarioGateway.buscarPorEmail("emanuelly@gmail.com"))
          .thenReturn(Optional.of(usuario));
      when(usuarioPerfilService.perfil(usuario))
          .thenReturn(PerfilUsuarioType.MORADOR);
      when(jwtService.gerarToken(usuario, PerfilUsuarioType.MORADOR))
          .thenReturn("token-jwt-abc");
      when(jwtService.getExpirationSeconds())
          .thenReturn(3600L);
      when(autenticacaoGateway.salvar(any(Autenticacao.class)))
          .thenReturn(new Autenticacao(1, 1, "token-jwt-abc", "bearer", null, 3600));

      SessaoAutenticada sessao = authService.login(credenciais);

      assertNotNull(sessao);
      assertEquals("token-jwt-abc", sessao.getToken());
      assertEquals("Bearer", sessao.getTipoToken());
      assertEquals(3600L, sessao.getExpiraEm().longValue());
      assertEquals(1, sessao.getUsuarioId());
      assertEquals("Emanuelly Mendes", sessao.getNome());
      assertEquals("emanuelly@gmail.com", sessao.getEmail());
      assertEquals(PerfilUsuarioType.MORADOR, sessao.getPerfil());

      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(usuarioGateway).buscarPorEmail("emanuelly@gmail.com");
      verify(autenticacaoGateway).salvar(any(Autenticacao.class));
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando senha estiver errada")
    void shouldThrowBadCredentialsWhenPasswordWrong() {
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenThrow(new BadCredentialsException("Credenciais inválidas"));

      assertThrows(BadCredentialsException.class,
          () -> authService.login(credenciais));

      verify(usuarioGateway, never()).buscarPorEmail(anyString());
      verify(autenticacaoGateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando usuário não existir")
    void shouldThrowNotFoundWhenUserNotExists() {
      Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenReturn(authentication);
      when(usuarioGateway.buscarPorEmail("emanuelly@gmail.com"))
          .thenReturn(Optional.empty());

      RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
          () -> authService.login(credenciais));

      assertEquals("Usuário não encontrado", ex.getMessage());
      verify(autenticacaoGateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve propagar a exceção quando authenticationManager falhar inesperadamente")
    void shouldPropagateExceptionWhenAuthManagerFailsUnexpectedly() {
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenThrow(new RuntimeException("falha interna"));

      assertThrows(RuntimeException.class,
          () -> authService.login(credenciais));

      verify(usuarioGateway, never()).buscarPorEmail(anyString());
      verify(autenticacaoGateway, never()).salvar(any());
    }
  }
}  