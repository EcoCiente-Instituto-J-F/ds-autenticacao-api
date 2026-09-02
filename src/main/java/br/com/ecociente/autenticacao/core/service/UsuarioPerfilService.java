package br.com.ecociente.autenticacao.core.service;


import java.util.Locale;

import org.springframework.stereotype.Service;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.Usuario;


@Service
public class UsuarioPerfilService {
  public PerfilUsuarioType perfil(Usuario usuario){
    String tipo = usuario.getTipoUsuario() == null ? "" : normalizar(usuario.getTipoUsuario());

    if (tipo.contains("cooperativa")){
      return PerfilUsuarioType.COOPERATIVA;
    } 
    else if(tipo.contains("sindico") || tipo.contains("síndico")){
      return PerfilUsuarioType.SINDICO;
    } 
    else if (tipo.contains("morador")) {
       return PerfilUsuarioType.MORADOR;  
    }
    return PerfilUsuarioType.USUARIO_COMUM;
    }

    private String normalizar(String tipoUsuario){
      return tipoUsuario == null ? "" : tipoUsuario.toLowerCase(Locale.ROOT);
    }
}

