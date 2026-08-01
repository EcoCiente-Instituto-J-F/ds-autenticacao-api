package br.com.ecociente.autenticacao.entrypoint.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.exception.RegraNegocioException;
import br.com.ecociente.autenticacao.entrypoint.dto.response.MensagemResponseDto;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(RegraNegocioException.class)
  ResponseEntity<MensagemResponseDto> handleRegraNegocio(RegraNegocioException exception) {
    return ResponseEntity.badRequest().body(new MensagemResponseDto(exception.getMessage()));
  }

  @ExceptionHandler(RecursoNaoEncontradoException.class)
  ResponseEntity<MensagemResponseDto> handleNaoEncontrado(RecursoNaoEncontradoException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensagemResponseDto(exception.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<MensagemResponseDto> handleBadCredentials() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MensagemResponseDto("E-mail ou senha invalidos"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<MensagemResponseDto> handleValidation(MethodArgumentNotValidException exception) {
    FieldError erro = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
    String mensagem = erro == null ? "Dados invalidos" : erro.getField() + ": " + erro.getDefaultMessage();
    return ResponseEntity.badRequest().body(new MensagemResponseDto(mensagem));
  }
}
