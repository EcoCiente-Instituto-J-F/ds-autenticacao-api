package br.com.ecociente.autenticacao.dataprovider.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ecociente.autenticacao.dataprovider.entity.TipoUsuarioEntity;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuarioEntity, Integer> {

  @Query("select tipo from TipoUsuarioEntity tipo where lower(tipo.nomeTipo) in :nomes")
  List<TipoUsuarioEntity> findByNomesNormalizados(@Param("nomes") Collection<String> nomes);
}
