package br.com.ecociente.autenticacao.entrypoint.dto;

import lombok.Builder;

/**
 * ValidationError
 */
@Builder
public record ValidationError(
  String field, 
  String message
) {

}
