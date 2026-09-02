package br.com.ecociente.autenticacao.entrypoint.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.exception.RegraNegocioException;
import br.com.ecociente.autenticacao.entrypoint.dto.ErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(RegraNegocioException.class)
  ResponseEntity<ErrorResponse> handleRegraNegocio(RegraNegocioException exception) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse(400, "REGRA_NEGOCIO", exception.getMessage()));
  }

  @ExceptionHandler(RecursoNaoEncontradoException.class)
  ResponseEntity<ErrorResponse> handleNaoEncontrado(RecursoNaoEncontradoException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(404, "RECURSO_NAO_ENCONTRADO", exception.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<ErrorResponse> handleBadCredentials() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.of(401, "CREDENCIAIS_INVALIDAS", "E-mail ou senha invalidos"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
    List<ErrorResponse.FieldError> erros = exception.getBindingResult().getFieldErrors().stream()
        .map(fe -> ErrorResponse.FieldError.of(fe.getField(), fe.getDefaultMessage()))
        .toList();
    return ResponseEntity.badRequest()
        .body(new ErrorResponse(400, "VALIDACAO", erros));
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponse> handleGenerico(Exception exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(500, "ERRO_INTERNO", "Erro interno do servidor"));
  }
}