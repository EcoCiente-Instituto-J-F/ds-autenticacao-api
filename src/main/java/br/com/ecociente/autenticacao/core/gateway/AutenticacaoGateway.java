package br.com.ecociente.autenticacao.core.gateway;

import br.com.ecociente.autenticacao.core.domain.Autenticacao;

public interface AutenticacaoGateway {
  Autenticacao salvar (Autenticacao autenticacao);
}
