package br.com.ecociente.autenticacao.dataprovider.mapper;

import org.springframework.stereotype.Component;

import br.com.ecociente.autenticacao.core.domain.Usuario;
import br.com.ecociente.autenticacao.dataprovider.entity.UsuarioEntity;

@Component 
public class UsuarioMapper {
  public Usuario toDomain(UsuarioEntity entity) {
    return new Usuario(
      entity.getId(),
      entity.getNome(),
      entity.getEmail(),
      entity.getSenhaHash(),
      entity.getStatus(),
      entity.getTipoUsuario().toString()
    );
  }
  
}
