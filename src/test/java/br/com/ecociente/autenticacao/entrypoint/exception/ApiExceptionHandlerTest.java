package br.com.ecociente.autenticacao.entrypoint.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.exception.RegraNegocioException;
import br.com.ecociente.autenticacao.entrypoint.dto.ErrorResponse;

class ApiExceptionHandlerTest {

  private ApiExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ApiExceptionHandler();
  }

  @Test
  @DisplayName("Deve retornar 400 com REGRA_NEGOCIO quando ocorrer RegraNegocioException")
  void shouldReturn400WhenRegraNegocioException() {
    RegraNegocioException ex = new RegraNegocioException("Operação inválida");

    ResponseEntity<ErrorResponse> response = handler.handleRegraNegocio(ex);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(400, response.getBody().status());
    assertEquals("REGRA_NEGOCIO", response.getBody().codigoError());
    assertEquals("Operação inválida", response.getBody().details().get(0).message());
  }

  @Test
  @DisplayName("Deve retornar 404 com RECURSO_NAO_ENCONTRADO quando ocorrer RecursoNaoEncontradoException")
  void shouldReturn404WhenRecursoNaoEncontradoException() {
    RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Usuário não encontrado");

    ResponseEntity<ErrorResponse> response = handler.handleNaoEncontrado(ex);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(404, response.getBody().status());
    assertEquals("RECURSO_NAO_ENCONTRADO", response.getBody().codigoError());
  }

  @Test
  @DisplayName("Deve retornar 401 com CREDENCIAIS_INVALIDAS quando ocorrer BadCredentialsException")
  void shouldReturn401WhenBadCredentials() {
    BadCredentialsException ex = new BadCredentialsException("senha errada");

    ResponseEntity<ErrorResponse> response = handler.handleBadCredentials();

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(401, response.getBody().status());
    assertEquals("CREDENCIAIS_INVALIDAS", response.getBody().codigoError());
  }

  @Test
  @DisplayName("Deve retornar 400 com VALIDACAO quando ocorrer MethodArgumentNotValidException")
  void shouldReturn400WhenValidationException() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(
        new org.springframework.validation.FieldError("login", "email", "Email é obrigatório"),
        new org.springframework.validation.FieldError("login", "senha", "Senha é obrigatória")));

    ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(400, response.getBody().status());
    assertEquals("VALIDACAO", response.getBody().codigoError());
    assertEquals(2, response.getBody().details().size());
    assertEquals("email", response.getBody().details().get(0).field());
    assertEquals("senha", response.getBody().details().get(1).field());
  }

  @Test
  @DisplayName("Deve retornar 500 com ERRO_INTERNO para exceção genérica")
  void shouldReturn500WhenGenericException() {
    RuntimeException ex = new RuntimeException("erro interno do banco");

    ResponseEntity<ErrorResponse> response = handler.handleGenerico(ex);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(500, response.getBody().status());
    assertEquals("ERRO_INTERNO", response.getBody().codigoError());
    assertEquals("Erro interno do servidor", response.getBody().details().get(0).message());
  }

  @Test
  @DisplayName("Não deve vazar a mensagem interna da exceção genérica no retorno")
  void shouldNotLeakInternalMessageInGenericFallback() {
    RuntimeException ex = new RuntimeException("detalhe sensível");

    ResponseEntity<ErrorResponse> response = handler.handleGenerico(ex);

    String mensagem = response.getBody().details().get(0).message();
    assertEquals("Erro interno do servidor", mensagem);
    assertFalse(mensagem.contains("detalhe sensível"));
  }
}