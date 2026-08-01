package br.com.ecociente.autenticacao;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecociente.autenticacao.core.service.AuthService;

import java.util.Map;

/**
 * Testes de integracao ponta a ponta: sobem o contexto Spring completo
 * (incluindo o filtro do Spring Security de verdade), um Postgres real
 * (Testcontainers) e chamam os endpoints via HTTP simulado (MockMvc),
 * exatamente como o Swagger/Thunder Client fariam.
 */
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AuthService authService;

  private MockMvc mockMvc() {
    return mockMvc;
  }

  private static final String PAYLOAD_REGISTRO_BASE = """
      {
        "nome": "%s",
        "email": "%s",
        "senha": "SenhaValida123!",
        "confirmarSenha": "SenhaValida123!",
        "perfil": "USUARIO_COMUM",
        "telefone": "11999998888",
        "cep": "01001000",
        "estado": "SP",
        "cidade": "Sao Paulo",
        "bairro": "Centro",
        "rua": "Rua das Flores",
        "numero": "123",
        "complemento": "Apto 101"
      }
      """;

  @Test
  void deveCadastrarUsuarioComumComSucesso() throws Exception {
    String payload = PAYLOAD_REGISTRO_BASE.formatted("Usuario Teste", "usuario.novo@teste.com");

    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token", notNullValue()))
        .andExpect(jsonPath("$.perfil").value("USUARIO_COMUM"))
        .andExpect(jsonPath("$.email").value("usuario.novo@teste.com"));
  }

  @Test
  void naoDeveCadastrarComEmailDuplicado() throws Exception {
    String payload = PAYLOAD_REGISTRO_BASE.formatted("Primeiro Cadastro", "duplicado@teste.com");

    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated());

    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.mensagem", notNullValue()));
  }

  @Test
  void naoDeveCadastrarComSenhasDiferentes() throws Exception {
    String payload = """
        {
          "nome": "Usuario Senha Invalida",
          "email": "senha.invalida@teste.com",
          "senha": "SenhaValida123!",
          "confirmarSenha": "SenhaDiferente123!",
          "perfil": "USUARIO_COMUM",
          "cep": "01001000",
          "estado": "SP",
          "cidade": "Sao Paulo",
          "rua": "Rua das Flores",
          "numero": "123"
        }
        """;

    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void naoDeveCadastrarSemCamposObrigatorios() throws Exception {
    String payload = """
        {
          "nome": "",
          "email": "email-invalido",
          "senha": "123",
          "confirmarSenha": "123"
        }
        """;

    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deveFazerLoginComCredenciaisValidas() throws Exception {
    String payload = PAYLOAD_REGISTRO_BASE.formatted("Usuario Login", "usuario.login@teste.com");
    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated());

    String loginPayload = """
        {
          "email": "usuario.login@teste.com",
          "senha": "SenhaValida123!"
        }
        """;

    mockMvc().perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token", notNullValue()))
        .andExpect(jsonPath("$.perfil").value("USUARIO_COMUM"));
  }

  @Test
  void naoDeveLogarComSenhaErrada() throws Exception {
    String payload = PAYLOAD_REGISTRO_BASE.formatted("Usuario Senha Errada", "senha.errada@teste.com");
    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated());

    String loginPayload = """
        {
          "email": "senha.errada@teste.com",
          "senha": "SenhaErrada123!"
        }
        """;

    mockMvc().perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginPayload))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void naoDeveLogarComEmailInexistente() throws Exception {
    String loginPayload = """
        {
          "email": "nao.existe@teste.com",
          "senha": "QualquerSenha123!"
        }
        """;

    mockMvc().perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginPayload))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void deveCadastrarCooperativaComSucesso() throws Exception {
    String payload = """
        {
          "nomeResponsavel": "Responsavel Cooperativa",
          "email": "cooperativa.teste@teste.com",
          "senha": "SenhaValida123!",
          "confirmarSenha": "SenhaValida123!",
          "cnpj": "12345678000199",
          "telefone": "11988887777",
          "cep": "04567000",
          "estado": "SP",
          "cidade": "Sao Paulo",
          "bairro": "Vila Mariana",
          "rua": "Rua Domingos de Morais",
          "numero": "500",
          "complemento": "Sala 12"
        }
        """;

    mockMvc().perform(post("/auth/register/cooperativa")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.perfil").value("COOPERATIVA"));
  }

  @Test
  void naoDeveCadastrarCooperativaComCnpjDuplicado() throws Exception {
    String payloadBase = """
        {
          "nomeResponsavel": "%s",
          "email": "%s",
          "senha": "SenhaValida123!",
          "confirmarSenha": "SenhaValida123!",
          "cnpj": "98765432000188",
          "telefone": "11988887777",
          "cep": "04567000",
          "estado": "SP",
          "cidade": "Sao Paulo",
          "bairro": "Vila Mariana",
          "rua": "Rua Domingos de Morais",
          "numero": "500",
          "complemento": "Sala 12"
        }
        """;

    mockMvc().perform(post("/auth/register/cooperativa")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payloadBase.formatted("Primeira Cooperativa", "cooperativa1@teste.com")))
        .andExpect(status().isCreated());

    mockMvc().perform(post("/auth/register/cooperativa")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payloadBase.formatted("Segunda Cooperativa", "cooperativa2@teste.com")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deveAtivarSindicoPreCadastrado() throws Exception {
    String payload = """
        {
          "email": "sindico.pre.cadastrado@teste.com",
          "senha": "NovaSenhaSindico123!",
          "confirmarSenha": "NovaSenhaSindico123!"
        }
        """;

    mockMvc().perform(post("/auth/ativar-sindico")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.perfil").value("SINDICO"));
  }

  @Test
  void naoDeveAtivarSindicoSemPreCadastro() throws Exception {
    String payload = """
        {
          "email": "nao.eh.sindico@teste.com",
          "senha": "NovaSenhaSindico123!",
          "confirmarSenha": "NovaSenhaSindico123!"
        }
        """;

    mockMvc().perform(post("/auth/ativar-sindico")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isNotFound());
  }

  @Test
  void deveSolicitarRecuperacaoESerCapazDeResetarSenha() throws Exception {
    String registroPayload = PAYLOAD_REGISTRO_BASE.formatted("Usuario Reset", "usuario.reset@teste.com");
    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registroPayload))
        .andExpect(status().isCreated());

    String esqueciSenhaPayload = """
        { "email": "usuario.reset@teste.com" }
        """;

    mockMvc().perform(post("/auth/esqueci-senha")
            .contentType(MediaType.APPLICATION_JSON)
            .content(esqueciSenhaPayload))
        .andExpect(status().isOk());

    // O codigo fica guardado em memoria dentro do AuthService (nao ha envio real
    // de e-mail). Pegamos o valor via reflection, do mesmo jeito que o
    // log temporario de debug fazia manualmente.
    @SuppressWarnings("unchecked")
    Map<String, Object> codigos = (Map<String, Object>) ReflectionTestUtils.getField(authService, "codigosRecuperacao");
    Object codigoRecuperacao = codigos.get("usuario.reset@teste.com");
    String codigo = (String) ReflectionTestUtils.invokeMethod(codigoRecuperacao, "codigo");

    String resetarSenhaPayload = """
        {
          "email": "usuario.reset@teste.com",
          "codigoRecuperacao": "%s",
          "novaSenha": "SenhaResetada123!",
          "confirmarNovaSenha": "SenhaResetada123!"
        }
        """.formatted(codigo);

    mockMvc().perform(post("/auth/resetar-senha")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resetarSenhaPayload))
        .andExpect(status().isOk());

    String loginComSenhaNova = """
        {
          "email": "usuario.reset@teste.com",
          "senha": "SenhaResetada123!"
        }
        """;

    mockMvc().perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginComSenhaNova))
        .andExpect(status().isOk());
  }

  @Test
  void naoDeveSolicitarRecuperacaoParaEmailInexistente() throws Exception {
    String payload = """
        { "email": "nao.existe.recuperacao@teste.com" }
        """;

    mockMvc().perform(post("/auth/esqueci-senha")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isNotFound());
  }

  @Test
  void naoDeveResetarSenhaComCodigoInvalido() throws Exception {
    String registroPayload = PAYLOAD_REGISTRO_BASE.formatted("Usuario Codigo Invalido", "codigo.invalido@teste.com");
    mockMvc().perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registroPayload))
        .andExpect(status().isCreated());

    String resetarSenhaPayload = """
        {
          "email": "codigo.invalido@teste.com",
          "codigoRecuperacao": "000000",
          "novaSenha": "SenhaResetada123!",
          "confirmarNovaSenha": "SenhaResetada123!"
        }
        """;

    mockMvc().perform(post("/auth/resetar-senha")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resetarSenhaPayload))
        .andExpect(status().isBadRequest());
  }
}
