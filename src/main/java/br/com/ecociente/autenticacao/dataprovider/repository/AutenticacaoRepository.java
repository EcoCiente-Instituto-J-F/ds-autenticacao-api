package br.com.ecociente.autenticacao.dataprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.autenticacao.dataprovider.entity.AutenticacaoEntity;

public interface AutenticacaoRepository extends JpaRepository<AutenticacaoEntity, Integer> {
    
}
