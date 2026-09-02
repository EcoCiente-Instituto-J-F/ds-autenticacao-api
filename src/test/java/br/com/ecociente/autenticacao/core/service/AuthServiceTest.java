// package br.com.ecociente.autenticacao.core.service;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Tag;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import br.com.ecociente.autenticacao.config.security.JwtService;
// import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
// import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
// import br.com.ecociente.autenticacao.core.exception.RegraNegocioException;
// import br.com.ecociente.autenticacao.dataprovider.entity.TipoUsuarioEntity;
// import br.com.ecociente.autenticacao.dataprovider.entity.UsuarioEntity;
// import br.com.ecociente.autenticacao.dataprovider.repository.CooperativaRepository;
// import br.com.ecociente.autenticacao.dataprovider.repository.EnderecoRepository;
// import br.com.ecociente.autenticacao.dataprovider.repository.SindicoRepository;
// import br.com.ecociente.autenticacao.dataprovider.repository.TelefoneRepository;
// import br.com.ecociente.autenticacao.dataprovider.repository.TipoUsuarioRepository;
// import br.com.ecociente.autenticacao.dataprovider.repository.UsuarioRepository;
// import br.com.ecociente.autenticacao.entrypoint.dto.request.CadastroUsuarioRequestDto;
// import br.com.ecociente.autenticacao.entrypoint.dto.request.LoginRequestDto;

// @Tag("unit")
// @ExtendWith(MockitoExtension.class)
// class AuthServiceTest {

//   @Mock
//   private AuthenticationManager authenticationManager;
//   @Mock
//   private PasswordEncoder passwordEncoder;
//   @Mock
//   private JwtService jwtService;
//   @Mock
//   private UsuarioPerfilService usuarioPerfilService;
//   @Mock
//   private UsuarioRepository usuarioRepository;
//   @Mock
//   private TipoUsuarioRepository tipoUsuarioRepository;
//   @Mock
//   private CooperativaRepository cooperativaRepository;
//   @Mock
//   private SindicoRepository sindicoRepository;
//   @Mock
//   private TelefoneRepository telefoneRepository;
//   @Mock
//   private EnderecoRepository enderecoRepository;
//   @Mock
//   private IdGeneratorService idGeneratorService;

//   private AuthService authService;

//   private static final CadastroUsuarioRequestDto REQUEST_VALIDO = new CadastroUsuarioRequestDto(
//       "Usuario Teste", "usuario@teste.com", "SenhaValida123!", "SenhaValida123!",
//       PerfilUsuarioType.USUARIO_COMUM, "11999998888",
//       "01001000", "SP", "Sao Paulo", "Centro", "Rua das Flores", "123", "Apto 101");

//   @BeforeEach
//   void setUp() {
//     authService = new AuthService(
//         authenticationManager, passwordEncoder, jwtService, usuarioPerfilService,
//         usuarioRepository, tipoUsuarioRepository, cooperativaRepository, sindicoRepository,
//         telefoneRepository, enderecoRepository, idGeneratorService);
//   }

//   @Test
//   void deveCadastrarUsuarioComumComSucesso() {
//     when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
//     when(idGeneratorService.proximoId(anyString(), anyString())).thenReturn(1);
//     when(passwordEncoder.encode(anyString())).thenReturn("hash-fake");
//     when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//     when(tipoUsuarioRepository.findByNomesNormalizados(any()))
//         .thenReturn(List.of(TipoUsuarioEntity.builder().id(1).nomeTipo("Comum").build()));
//     when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//     when(jwtService.gerarToken(any(), any())).thenReturn("token-fake");
//     when(jwtService.getExpirationSeconds()).thenReturn(7200L);

//     var response = authService.cadastrarUsuario(REQUEST_VALIDO);

//     assertThat(response.token()).isEqualTo("token-fake");
//     assertThat(response.perfil()).isEqualTo(PerfilUsuarioType.USUARIO_COMUM);
//     verify(telefoneRepository).save(any());
//   }

//   @Test
//   void naoDeveCadastrarComEmailJaExistente() {
//     when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

//     assertThatThrownBy(() -> authService.cadastrarUsuario(REQUEST_VALIDO))
//         .isInstanceOf(RegraNegocioException.class)
//         .hasMessageContaining("E-mail ja cadastrado");

//     verify(usuarioRepository, never()).save(any());
//   }

//   @Test
//   void naoDeveCadastrarComSenhasDiferentes() {
//     var requestSenhaDiferente = new CadastroUsuarioRequestDto(
//         "Usuario Teste", "usuario@teste.com", "SenhaValida123!", "SenhaDiferente123!",
//         PerfilUsuarioType.USUARIO_COMUM, "11999998888",
//         "01001000", "SP", "Sao Paulo", "Centro", "Rua das Flores", "123", "Apto 101");

//     assertThatThrownBy(() -> authService.cadastrarUsuario(requestSenhaDiferente))
//         .isInstanceOf(RegraNegocioException.class)
//         .hasMessageContaining("nao conferem");

//     verify(usuarioRepository, never()).existsByEmailIgnoreCase(anyString());
//   }

//   @Test
//   void naoDeveCadastrarComoSindicoOuCooperativaPeloEndpointComum() {
//     var requestSindico = new CadastroUsuarioRequestDto(
//         "Usuario Teste", "usuario@teste.com", "SenhaValida123!", "SenhaValida123!",
//         PerfilUsuarioType.SINDICO, "11999998888",
//         "01001000", "SP", "Sao Paulo", "Centro", "Rua das Flores", "123", "Apto 101");
//     when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);

//     assertThatThrownBy(() -> authService.cadastrarUsuario(requestSindico))
//         .isInstanceOf(RegraNegocioException.class);
//   }

//   @Test
//   void naoDeveCadastrarQuandoTipoDeUsuarioNaoEstaConfiguradoNoBanco() {
//     when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
//     when(idGeneratorService.proximoId(anyString(), anyString())).thenReturn(1);
//     when(enderecoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//     when(tipoUsuarioRepository.findByNomesNormalizados(any())).thenReturn(List.of());
//     when(tipoUsuarioRepository.findAll()).thenReturn(List.of());

//     assertThatThrownBy(() -> authService.cadastrarUsuario(REQUEST_VALIDO))
//         .isInstanceOf(RegraNegocioException.class)
//         .hasMessageContaining("Tipo de usuario nao configurado");
//   }

//   @Test
//   void deveFazerLoginComSucesso() {
//     var usuario = UsuarioEntity.builder().id(1).nome("Usuario Teste").email("usuario@teste.com").build();
//     when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com")).thenReturn(Optional.of(usuario));
//     when(usuarioPerfilService.resolverPerfil(usuario)).thenReturn(PerfilUsuarioType.USUARIO_COMUM);
//     when(jwtService.gerarToken(any(), any())).thenReturn("token-fake");
//     when(jwtService.getExpirationSeconds()).thenReturn(7200L);

//     var response = authService.login(new LoginRequestDto("usuario@teste.com", "SenhaValida123!"));

//     assertThat(response.token()).isEqualTo("token-fake");
//     assertThat(response.perfil()).isEqualTo(PerfilUsuarioType.USUARIO_COMUM);
//   }

//   @Test
//   void devePropagarCredenciaisInvalidasNoLogin() {
//     org.mockito.Mockito.doThrow(new BadCredentialsException("invalido"))
//         .when(authenticationManager).authenticate(any());

//     assertThatThrownBy(() -> authService.login(new LoginRequestDto("usuario@teste.com", "SenhaErrada123!")))
//         .isInstanceOf(BadCredentialsException.class);
//   }

//   @Test
//   void naoDeveSolicitarRecuperacaoParaEmailInexistente() {
//     when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);

//     assertThatThrownBy(() -> authService.solicitarRecuperacaoSenha(
//         new br.com.ecociente.autenticacao.entrypoint.dto.request.RecuperacaoSenhaRequestDto("nao.existe@teste.com")))
//         .isInstanceOf(RecursoNaoEncontradoException.class);
//   }
// }
