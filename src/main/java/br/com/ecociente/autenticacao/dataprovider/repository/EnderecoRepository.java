package br.com.ecociente.autenticacao.dataprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.autenticacao.dataprovider.entity.EnderecoEntity;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Integer> {
}
