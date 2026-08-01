package br.com.ecociente.autenticacao.dataprovider.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.autenticacao.dataprovider.entity.SindicoEntity;
import br.com.ecociente.autenticacao.dataprovider.entity.UsuarioEntity;

public interface SindicoRepository extends JpaRepository<SindicoEntity, Integer> {
  Optional<SindicoEntity> findByUsuario(UsuarioEntity usuario);
}
