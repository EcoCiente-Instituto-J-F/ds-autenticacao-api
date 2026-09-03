// package br.com.ecociente.autenticacao.config.security;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.UUID;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.springframework.test.util.ReflectionTestUtils;

// import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
// import br.com.ecociente.autenticacao.core.domain.Usuario;

// class JwtServiceTest {

//   private JwtService jwtService;

//   private static final String SECRET = UUID.randomUUID().toString();
//   private static final long EXPIRATION_SECONDS = 3600L;

//   @BeforeEach
//   void setUp() {
//     jwtService = new JwtService(SECRET, EXPIRATION_SECONDS);
//     ReflectionTestUtils.setField(jwtService, "secret", SECRET);
//     ReflectionTestUtils.setField(jwtService, "expirationSeconds", EXPIRATION_SECONDS);
//   }

//   private Usuario criarUsuario() {
//     return new Usuario(1, "Nome", "email@teste.com", "hash", true, "morador");
//   }

//   @Nested
//   @DisplayName("gerarToken")
//   class GerarToken {

//     @Test
//     @DisplayName("Deve gerar token não vazio")
//     void shouldGenerateNonEmptyToken() {
//       Usuario usuario = criarUsuario();

//       String token = jwtService.gerarToken(usuario, PerfilUsuarioType.MORADOR);

//       assertNotNull(token);
//       assertTrue(token.length() > 0);
//     }

//     @Test
//     @DisplayName("Deve gerar tokens diferentes para usuários diferentes")
//     void shouldGenerateDifferentTokensForDifferentUsers() {
//       Usuario usuario1 = new Usuario(1, "Nome1", "a@teste.com", "hash", true, "morador");
//       Usuario usuario2 = new Usuario(2, "Nome2", "b@teste.com", "hash", true, "sindico");

//       String token1 = jwtService.gerarToken(usuario1, PerfilUsuarioType.MORADOR);
//       String token2 = jwtService.gerarToken(usuario2, PerfilUsuarioType.SINDICO);

//       assertTrue(!token1.equals(token2));
//     }
//   }

//   @Nested
//   @DisplayName("getExpirationSeconds")
//   class GetExpirationSeconds {

//     @Test
//     @DisplayName("Deve retornar o valor configurado de expiração")
//     void shouldReturnConfiguredExpiration() {
//       assertEquals(EXPIRATION_SECONDS, jwtService.getExpirationSeconds());
//     }
//   }

//   @Nested
//   @DisplayName("validarToken")
//   class ValidarToken {

//     @Test
//     @DisplayName("Deve retornar o email correto quando token é válido")
//     void shouldReturnEmailWhenTokenValid() {
//       Usuario usuario = criarUsuario();
//       String token = jwtService.gerarToken(usuario, PerfilUsuarioType.MORADOR);

//       String email = jwtService.isTokenValido(token, usuario.getEmail());

//       assertEquals("email@teste.com", email);
//     }

//     @Test
//     @DisplayName("Deve lançar exceção quando token for inválido")
//     void shouldThrowWhenTokenInvalid() {
//       assertThrows(Exception.class,
//           () -> jwtService.isTokenValido("token-invalido", "email@teste.com"));
//     }

//     @Test
//     @DisplayName("Deve lançar exceção quando token for nulo")
//     void shouldThrowWhenTokenNull() {
//       assertThrows(Exception.class,
//           () -> jwtService.isTokenValido(null, "email@teste.com"));
//     }
//   }
// }