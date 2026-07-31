package br.com.ecociente.autenticacao.core.service;

import java.util.Locale;

import org.springframework.stereotype.Service;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.dataprovider.entity.UsuarioEntity;

@Service
public class UsuarioPerfilService {

  public PerfilUsuarioType resolverPerfil(UsuarioEntity usuario) {
    String nomeTipo = usuario.getTipoUsuario() == null ? "" : normalizar(usuario.getTipoUsuario().getNomeTipo());

    if (nomeTipo.contains("cooperativa")) {
      return PerfilUsuarioType.COOPERATIVA;
    }
    if (nomeTipo.contains("sindico") || nomeTipo.contains("síndico")) {
      return PerfilUsuarioType.SINDICO;
    }
    if (nomeTipo.contains("morador")) {
      return PerfilUsuarioType.MORADOR;
    }
    return PerfilUsuarioType.USUARIO_COMUM;
  }

  private String normalizar(String valor) {
    return valor == null ? "" : valor.toLowerCase(Locale.ROOT);
  }
}
