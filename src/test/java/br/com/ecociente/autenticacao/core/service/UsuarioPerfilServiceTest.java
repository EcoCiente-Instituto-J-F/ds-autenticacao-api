package br.com.ecociente.autenticacao.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.Usuario;

class UsuarioPerfilServiceTest {

  private UsuarioPerfilService usuarioPerfilService;

  @BeforeEach
  void setUp() {
    usuarioPerfilService = new UsuarioPerfilService();
  }

  private Usuario criarUsuario(String tipoNome) {
    return new Usuario(1, "nome", "email@teste.com", "hash", true, tipoNome);
  }

  @Nested
  @DisplayName("resolverPerfil")
  class ResolverPerfil {

    @Test
    @DisplayName("Deve retornar COOPERATIVA quando tipo contém 'cooperativa'")
    void shouldReturnCooperativa() {
      Usuario usuario = criarUsuario("cooperativa");

      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      assertEquals(PerfilUsuarioType.COOPERATIVA, perfil);
    }

    @Test
    @DisplayName("Deve retornar SINDICO quando tipo contém 'síndico' (com acento)")
    void shouldReturnSindicoComAcento() {
      Usuario usuario = criarUsuario("síndico");

      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      assertEquals(PerfilUsuarioType.SINDICO, perfil);
    }

    @Test
    @DisplayName("Deve retornar SINDICO quando tipo contém 'sindico' (sem acento)")
    void shouldReturnSindicoSemAcento() {
      Usuario usuario = criarUsuario("sindico");

      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      assertEquals(PerfilUsuarioType.SINDICO, perfil);
    }

    @Test
    @DisplayName("Deve retornar MORADOR quando tipo contém 'morador'")
    void shouldReturnMorador() {
      Usuario usuario = criarUsuario("morador");

      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      assertEquals(PerfilUsuarioType.MORADOR, perfil);
    }

    @Test
    @DisplayName("Deve retornar USUARIO_COMUM quando tipo não corresponde a nenhum perfil específico")
    void shouldReturnUsuarioComum() {
      Usuario usuario = criarUsuario("admin");

      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      assertEquals(PerfilUsuarioType.USUARIO_COMUM, perfil);
    }

    @Test
    @DisplayName("Deve retornar USUARIO_COMUM quando tipo é nulo")
    void shouldReturnUsuarioComumWhenTipoIsNull() {
      Usuario usuario = criarUsuario(null);

      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      assertEquals(PerfilUsuarioType.USUARIO_COMUM, perfil);
    }

    @Test
    @DisplayName("Deve ser case-insensitive ao resolver o perfil")
    void shouldBeCaseInsensitive() {
      Usuario usuario = criarUsuario("MORADOR");

      PerfilUsuarioType perfil = usuarioPerfilService.perfil(usuario);

      assertEquals(PerfilUsuarioType.MORADOR, perfil);
    }
  }
}