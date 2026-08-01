package br.com.ecociente.autenticacao.dataprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.autenticacao.dataprovider.entity.CondominioEntity;

public interface CondominioRepository extends JpaRepository<CondominioEntity, Integer> {
}
