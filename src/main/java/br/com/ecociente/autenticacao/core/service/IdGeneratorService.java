package br.com.ecociente.autenticacao.core.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * O banco de dados desta API usa colunas "INT PRIMARY KEY" sem auto-incremento
 * (SERIAL/IDENTITY). Como o schema do banco nao pode ser alterado, a propria
 * aplicacao calcula o proximo ID antes de cada insercao.
 *
 * Um advisory lock do Postgres (escopo de transacao) evita que duas
 * requisicoes simultaneas calculem o mesmo "MAX(id) + 1" e colidam.
 */
@Service
@RequiredArgsConstructor
public class IdGeneratorService {

  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public Integer proximoId(String tabela, String coluna) {
    jdbcTemplate.execute("SELECT pg_advisory_xact_lock(hashtext('" + tabela + "'))");
    Integer proximo = jdbcTemplate.queryForObject(
        "SELECT COALESCE(MAX(" + coluna + "), 0) + 1 FROM " + tabela, Integer.class);
    return proximo;
  }
}
