package br.com.ecociente.autenticacao.dataprovider.mapper;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import br.com.ecociente.autenticacao.core.domain.Autenticacao;
import br.com.ecociente.autenticacao.dataprovider.entity.AutenticacaoEntity;

@Component 
public class AutenticacaoMapper {
  public Autenticacao toDomain(AutenticacaoEntity entity) {
    return new Autenticacao(
      entity.getId(),
      entity.getUsuarioId(),
      entity.getToken(),
      entity.getTipoToken(),
      entity.getCriadoEm(),
      entity.getExpiradoEm()
    );
  }

  public AutenticacaoEntity toEntity(Autenticacao domain) {
    return AutenticacaoEntity.builder()
      .usuarioId(domain.getUsuarioId())
      .token(domain.getToken())
      .tipoToken(domain.getTipoToken())
      .criadoEm(OffsetDateTime.now())
      .expiradoEm(domain.getExpiradoEm())
      .build();
  }
}
