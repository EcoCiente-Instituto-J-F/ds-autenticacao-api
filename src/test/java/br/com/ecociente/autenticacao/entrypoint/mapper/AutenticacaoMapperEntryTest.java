package br.com.ecociente.autenticacao.entrypoint.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.domain.SessaoAutenticada;
import br.com.ecociente.autenticacao.core.domain.UsuarioCredenciais;
import br.com.ecociente.autenticacao.entrypoint.dto.request.LoginRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.response.AuthResponseDto;

class AutenticacaoMapperEntryTest {

  private AutenticacaoMapperEntry mapper;

  @BeforeEach
  void setUp() {
    mapper = new AutenticacaoMapperEntry();
  }

  @Nested
  @DisplayName("toDomain")
  class ToDomain {

    @Test
    @DisplayName("Deve mapear LoginRequestDto para UsuarioCredenciais")
    void shouldMapLoginRequestToCredenciais() {
      LoginRequestDto dto = new LoginRequestDto("emanuelly@gmail.com", "Senha@T3ste");

      UsuarioCredenciais credenciais = mapper.toDomain(dto);

      assertNotNull(credenciais);
      assertEquals("emanuelly@gmail.com", credenciais.getEmail());
      assertEquals("Senha@T3ste", credenciais.getSenha());
    }

    @Test
    @DisplayName("Deve retornar null quando DTO for null")
    void shouldReturnNullWhenDtoIsNull() {
      assertNull(mapper.toDomain(null));
    }
  }

  @Nested
  @DisplayName("toResponse")
  class ToResponse {

    @Test
    @DisplayName("Deve mapear SessaoAutenticada para AuthResponseDto")
    void shouldMapSessaoToAuthResponse() {
      SessaoAutenticada sessao = new SessaoAutenticada(
          "token-abc",
          "Bearer",
          3600,
          10,
          "Emanuelly Mendes",
          "emanuelly@gmail.com",
          PerfilUsuarioType.MORADOR);

      AuthResponseDto dto = mapper.toResponse(sessao);

      assertNotNull(dto);
      assertEquals("token-abc", dto.token());
      assertEquals("Bearer", dto.tipoToken());
      assertEquals(3600L, dto.expiraEmSegundos());
      assertEquals(10, dto.usuarioId());
      assertEquals("Emanuelly Mendes", dto.nome());
      assertEquals("emanuelly@gmail.com", dto.email());
      assertEquals(PerfilUsuarioType.MORADOR, dto.perfil());
    }

    @Test
    @DisplayName("Deve retornar null quando sessão for null")
    void shouldReturnNullWhenSessaoIsNull() {
      assertNull(mapper.toResponse(null));
    }
  }
}