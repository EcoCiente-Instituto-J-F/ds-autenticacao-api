package br.com.ecociente.autenticacao;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import br.com.ecociente.autenticacao.config.security.JwtService;

@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthControllerIntegrationTest extends AbstractIntegrationTest {
  private static final String SENHA = "SenhaValida123!";
  @Autowired private MockMvc mvc;
  @Autowired private JdbcTemplate jdbc;
  @Autowired private ObjectMapper json;
  @Autowired private PasswordEncoder encoder;
  @Autowired private JwtService jwtService;

  @BeforeEach
  void preparar() {
    String hash = encoder.encode(SENHA);
    assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    assertTrue(hash.startsWith("$2"));
    criarUsuario(1,"Comum Teste","comum@teste.com",1,true,hash);
    criarUsuario(2,"Moradora Teste","morador@teste.com",2,true,hash);
    criarUsuario(3,"Sindico Teste","sindico@teste.com",3,true,hash);
    criarUsuario(4,"Cooperativa Teste","cooperativa@teste.com",4,true,hash);
    criarUsuario(5,"Inativo Teste","inativo@teste.com",1,false,hash);
  }

  private void criarUsuario(int id,String nome,String email,int tipo,boolean ativo,String hash) {
    jdbc.update("""
      INSERT INTO tb_usuarios(id_usuario,nome_usuario,email_usuario,senha_hash,ativo,tipo_usuario_id,endereco_id)
      OVERRIDING SYSTEM VALUE VALUES (?,?,?,?,?,?,1)
      """,id,nome,email,hash,ativo,tipo);
  }
  private String payload(String email,String senha) throws Exception {
    return json.writeValueAsString(java.util.Map.of("email",email,"senha",senha));
  }
  @Test void deveAutenticarBCryptEPersistirToken() throws Exception {
    var resposta=mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
       .content(payload("comum@teste.com",SENHA)))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.usuarioId").value(1))
       .andExpect(jsonPath("$.perfil").value("USUARIO_COMUM"))
       .andExpect(jsonPath("$.tipoToken").value("Bearer")).andReturn();
    JsonNode corpo=json.readTree(resposta.getResponse().getContentAsString());
    String jwt=corpo.get("token").asText();
    assertTrue(jwtService.isTokenValido(jwt));
    assertEquals("comum@teste.com",jwtService.extrairEmail(jwt));
    assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM tb_autenticacoes_api WHERE usuario_id=1",Integer.class));
    String hashToken=jdbc.queryForObject("SELECT token FROM tb_autenticacoes_api WHERE usuario_id=1",String.class);
    assertNotNull(hashToken);
    assertTrue(hashToken.matches("[0-9a-f]{64}"),"SHA-256 deve proteger somente o token armazenado");
    assertNotEquals(jwt,hashToken);
    assertEquals("Bearer",jdbc.queryForObject("SELECT tipo_token FROM tb_autenticacoes_api WHERE usuario_id=1",String.class));
  }
  @Test void deveReconhecerPerfis() throws Exception {
    String[][] casos={{"comum@teste.com","USUARIO_COMUM"},{"morador@teste.com","MORADOR"},{"sindico@teste.com","SINDICO"},{"cooperativa@teste.com","COOPERATIVA"}};
    for(String[] caso:casos)mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
       .content(payload(caso[0],SENHA))).andExpect(status().isOk()).andExpect(jsonPath("$.perfil").value(caso[1]));
  }
  @Test void naoDeveAutenticarSenhaIncorreta() throws Exception {
    mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload("comum@teste.com","SenhaErrada123!")))
       .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.codigoError").value("CREDENCIAIS_INVALIDAS"));
    assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM tb_autenticacoes_api",Integer.class));
  }
  @Test void naoDeveAutenticarEmailInexistente() throws Exception {
    mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload("inexistente@teste.com",SENHA)))
       .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.codigoError").value("CREDENCIAIS_INVALIDAS"));
  }
  @Test void naoDeveAutenticarUsuarioInativo() throws Exception {
    mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload("inativo@teste.com",SENHA)))
       .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.codigoError").value("CREDENCIAIS_INVALIDAS"));
  }
  @Test void naoDeveAceitarSenhaVazia() throws Exception {
    mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload("comum@teste.com","")))
       .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigoError").value("VALIDACAO"));
  }
  @Test void naoDeveAceitarEmailInvalido() throws Exception {
    mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload("email-invalido",SENHA)))
       .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigoError").value("VALIDACAO"));
  }
  @Test void naoDeveAceitarJsonInvalido() throws Exception {
    mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{json invalido"))
       .andExpect(status().isBadRequest());
  }
}
