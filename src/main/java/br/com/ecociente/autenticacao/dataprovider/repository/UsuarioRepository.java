package br.com.ecociente.autenticacao.dataprovider.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.autenticacao.dataprovider.entity.UsuarioEntity;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
  Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);
}
