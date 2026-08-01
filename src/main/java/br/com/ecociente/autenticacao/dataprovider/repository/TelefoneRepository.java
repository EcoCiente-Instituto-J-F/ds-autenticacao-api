package br.com.ecociente.autenticacao.dataprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.autenticacao.dataprovider.entity.TelefoneEntity;

public interface TelefoneRepository extends JpaRepository<TelefoneEntity, Integer> {
}
