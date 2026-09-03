package br.com.ecociente.autenticacao.entrypoint.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecociente.autenticacao.config.security.JwtService;
import br.com.ecociente.autenticacao.config.security.UsuarioDetailsService;
import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.SessaoAutenticada;
import br.com.ecociente.autenticacao.core.domain.UsuarioCredenciais;
import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.service.AuthService;
import br.com.ecociente.autenticacao.entrypoint.exception.ApiExceptionHandler;
import br.com.ecociente.autenticacao.entrypoint.mapper.AutenticacaoMapperEntry;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
    ApiExceptionHandler.class,
    AutenticacaoMapperEntry.class
})
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean 
  private AuthService authService;

  @MockitoBean 
  private JwtService jwtService;

  @MockitoBean 
  private UsuarioDetailsService usuarioDetailsService;

  private static final String JSON_VALIDO = """
      {
        "email": "emanuelly@gmail.com",
        "senha": "Senha@T3ste"
      }
      """;

  @Nested
  @DisplayName("POST /auth/login")
  class Login {

    @Test
    @DisplayName("Deve retornar 200 com token quando credenciais válidas")
    void shouldReturn200WithTokenWhenCredentialsValid() throws Exception {
      SessaoAutenticada sessao = new SessaoAutenticada(
          "token-abc",
          "Bearer",
          3600,
          10,
          "Emanuelly Mendes",
          "emanuelly@gmail.com",
          PerfilUsuarioType.MORADOR);

      when(authService.login(any(UsuarioCredenciais.class))).thenReturn(sessao);

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(JSON_VALIDO))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value("token-abc"))
          .andExpect(jsonPath("$.tipoToken").value("Bearer"))
          .andExpect(jsonPath("$.expiraEmSegundos").value(3600))
          .andExpect(jsonPath("$.usuarioId").value(10))
          .andExpect(jsonPath("$.nome").value("Emanuelly Mendes"))
          .andExpect(jsonPath("$.email").value("emanuelly@gmail.com"))
          .andExpect(jsonPath("$.perfil").value("MORADOR"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando email estiver ausente")
    void shouldReturn400WhenEmailMissing() throws Exception {
      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "email": "",
                    "senha": "Senha@T3ste"
                  }
                  """))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.codigoError").value("VALIDACAO"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando senha estiver ausente")
    void shouldReturn400WhenPasswordMissing() throws Exception {
      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "email": "emanuelly@gmail.com",
                    "senha": ""
                  }
                  """))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.codigoError").value("VALIDACAO"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando JSON for inválido")
    void shouldReturn400WhenJsonInvalid() throws Exception {
      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{ json invalido"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 quando usuário não for encontrado")
    void shouldReturn404WhenUserNotFound() throws Exception {
      when(authService.login(any(UsuarioCredenciais.class)))
          .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(JSON_VALIDO))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.codigoError").value("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    @DisplayName("Deve retornar 500 quando ocorrer erro interno")
    void shouldReturn500WhenInternalError() throws Exception {
      when(authService.login(any(UsuarioCredenciais.class)))
          .thenThrow(new RuntimeException("erro inesperado"));

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(JSON_VALIDO))
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.status").value(500))
          .andExpect(jsonPath("$.codigoError").value("ERRO_INTERNO"));
    }
  }
}