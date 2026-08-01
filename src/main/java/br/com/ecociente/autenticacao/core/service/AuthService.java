package br.com.ecociente.autenticacao.core.service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ecociente.autenticacao.config.security.JwtService;
import br.com.ecociente.autenticacao.core.domain.PerfilUsuarioType;
import br.com.ecociente.autenticacao.core.exception.RecursoNaoEncontradoException;
import br.com.ecociente.autenticacao.core.exception.RegraNegocioException;
import br.com.ecociente.autenticacao.dataprovider.entity.CooperativaEntity;
import br.com.ecociente.autenticacao.dataprovider.entity.EnderecoEntity;
import br.com.ecociente.autenticacao.dataprovider.entity.TelefoneEntity;
import br.com.ecociente.autenticacao.dataprovider.entity.TipoUsuarioEntity;
import br.com.ecociente.autenticacao.dataprovider.entity.UsuarioEntity;
import br.com.ecociente.autenticacao.dataprovider.repository.CooperativaRepository;
import br.com.ecociente.autenticacao.dataprovider.repository.EnderecoRepository;
import br.com.ecociente.autenticacao.dataprovider.repository.SindicoRepository;
import br.com.ecociente.autenticacao.dataprovider.repository.TelefoneRepository;
import br.com.ecociente.autenticacao.dataprovider.repository.TipoUsuarioRepository;
import br.com.ecociente.autenticacao.dataprovider.repository.UsuarioRepository;
import br.com.ecociente.autenticacao.entrypoint.dto.request.AtivacaoSindicoRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.CadastroCooperativaRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.CadastroUsuarioRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.LoginRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.RecuperacaoSenhaRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.ResetarSenhaRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.response.AuthResponseDto;
import br.com.ecociente.autenticacao.entrypoint.dto.response.MensagemResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final Integer MINUTOS_VALIDADE_CODIGO = 15;

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UsuarioPerfilService usuarioPerfilService;
  private final UsuarioRepository usuarioRepository;
  private final TipoUsuarioRepository tipoUsuarioRepository;
  private final CooperativaRepository cooperativaRepository;
  private final SindicoRepository sindicoRepository;
  private final TelefoneRepository telefoneRepository;
  private final EnderecoRepository enderecoRepository;
  private final IdGeneratorService idGeneratorService;
  private final Map<String, CodigoRecuperacao> codigosRecuperacao = new ConcurrentHashMap<>();

  public AuthResponseDto login(LoginRequestDto request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

    UsuarioEntity usuario = buscarUsuarioPorEmail(request.email());
    PerfilUsuarioType perfil = usuarioPerfilService.resolverPerfil(usuario);
    return criarAuthResponse(usuario, perfil);
  }

  @Transactional
  public AuthResponseDto cadastrarUsuario(CadastroUsuarioRequestDto request) {
    validarSenhasIguais(request.senha(), request.confirmarSenha());
    validarEmailDisponivel(request.email());

    if (request.perfil() == PerfilUsuarioType.SINDICO || request.perfil() == PerfilUsuarioType.COOPERATIVA) {
      throw new RegraNegocioException("Use o fluxo especifico para sindico ou cooperativa");
    }

    EnderecoEntity endereco = salvarEndereco(request.cep(), request.estado(), request.cidade(),
        request.bairro(), request.rua(), request.numero(), request.complemento());

    UsuarioEntity usuario = UsuarioEntity.builder()
        .id(idGeneratorService.proximoId("usuario", "id_usuario"))
        .nome(request.nome())
        .email(request.email().trim().toLowerCase(Locale.ROOT))
        .senhaHash(passwordEncoder.encode(request.senha()))
        .status(true)
        .endereco(endereco)
        .tipoUsuario(buscarTipoUsuario(request.perfil()))
        .build();
    usuario = usuarioRepository.save(usuario);

    salvarTelefoneSeInformado(usuario, request.telefone());
    return criarAuthResponse(usuario, request.perfil());
  }

  @Transactional
  public AuthResponseDto cadastrarCooperativa(CadastroCooperativaRequestDto request) {
    validarSenhasIguais(request.senha(), request.confirmarSenha());
    validarEmailDisponivel(request.email());
    if (cooperativaRepository.existsByCnpj(request.cnpj())) {
      throw new RegraNegocioException("CNPJ ja cadastrado");
    }

    EnderecoEntity endereco = salvarEndereco(request.cep(), request.estado(), request.cidade(),
        request.bairro(), request.rua(), request.numero(), request.complemento());

    UsuarioEntity usuario = UsuarioEntity.builder()
        .id(idGeneratorService.proximoId("usuario", "id_usuario"))
        .nome(request.nomeResponsavel())
        .email(request.email().trim().toLowerCase(Locale.ROOT))
        .senhaHash(passwordEncoder.encode(request.senha()))
        .status(true)
        .endereco(endereco)
        .tipoUsuario(buscarTipoUsuario(PerfilUsuarioType.COOPERATIVA))
        .build();
    usuario = usuarioRepository.save(usuario);

    cooperativaRepository.save(CooperativaEntity.builder()
        .id(idGeneratorService.proximoId("cooperativa", "id_cooperativa"))
        .usuario(usuario)
        .cnpj(request.cnpj())
        .endereco(endereco)
        .build());

    salvarTelefoneSeInformado(usuario, request.telefone());
    return criarAuthResponse(usuario, PerfilUsuarioType.COOPERATIVA);
  }

  @Transactional
  public AuthResponseDto ativarSindico(AtivacaoSindicoRequestDto request) {
    validarSenhasIguais(request.senha(), request.confirmarSenha());
    UsuarioEntity usuario = buscarUsuarioPorEmail(request.email());
    sindicoRepository.findByUsuario(usuario)
        .orElseThrow(() -> new RegraNegocioException("Conta de sindico nao encontrada para este e-mail"));

    usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
    usuario.setStatus(true);
    usuarioRepository.save(usuario);

    return criarAuthResponse(usuario, PerfilUsuarioType.SINDICO);
  }

  public MensagemResponseDto solicitarRecuperacaoSenha(RecuperacaoSenhaRequestDto request) {
    if (!usuarioRepository.existsByEmailIgnoreCase(request.email())) {
      throw new RecursoNaoEncontradoException("E-mail nao encontrado");
    }
    String codigo = gerarCodigoRecuperacao();
    codigosRecuperacao.put(
        normalizarEmail(request.email()),
        new CodigoRecuperacao(codigo, OffsetDateTime.now().plusMinutes(MINUTOS_VALIDADE_CODIGO)));

    return new MensagemResponseDto("Solicitacao de recuperacao registrada para envio de link ou codigo");
  }

  @Transactional
  public MensagemResponseDto resetarSenha(ResetarSenhaRequestDto request) {
    validarSenhasIguais(request.novaSenha(), request.confirmarNovaSenha());
    validarCodigoRecuperacao(request.email(), request.codigoRecuperacao());

    UsuarioEntity usuario = buscarUsuarioPorEmail(request.email());
    usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
    usuarioRepository.save(usuario);
    codigosRecuperacao.remove(normalizarEmail(request.email()));

    return new MensagemResponseDto("Senha redefinida com sucesso");
  }

  private UsuarioEntity buscarUsuarioPorEmail(String email) {
    return usuarioRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
  }

  private void validarCodigoRecuperacao(String email, String codigo) {
    CodigoRecuperacao codigoSalvo = codigosRecuperacao.get(normalizarEmail(email));
    if (codigoSalvo == null || codigoSalvo.expiraEm().isBefore(OffsetDateTime.now()) || !codigoSalvo.codigo().equals(codigo)) {
      throw new RegraNegocioException("Codigo de recuperacao invalido ou expirado");
    }
  }

  private void validarEmailDisponivel(String email) {
    if (usuarioRepository.existsByEmailIgnoreCase(email)) {
      throw new RegraNegocioException("E-mail ja cadastrado");
    }
  }

  private String gerarCodigoRecuperacao() {
    return String.valueOf(100000 + new Random().nextInt(900000));
  }

  private String normalizarEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private void validarSenhasIguais(String senha, String confirmacao) {
    if (!senha.equals(confirmacao)) {
      throw new RegraNegocioException("Senha e confirmacao de senha nao conferem");
    }
  }

  private TipoUsuarioEntity buscarTipoUsuario(PerfilUsuarioType perfil) {
    List<String> aliases = perfil.getNomesTipo().stream()
        .map(nome -> nome.toLowerCase(Locale.ROOT))
        .toList();

    List<TipoUsuarioEntity> encontrados = tipoUsuarioRepository.findByNomesNormalizados(aliases);
    if (!encontrados.isEmpty()) {
      return encontrados.get(0);
    }

    List<TipoUsuarioEntity> todosTipos = tipoUsuarioRepository.findAll();
    String registrosBanco = todosTipos.stream()
        .map(t -> "ID " + t.getId() + ": " + t.getNomeTipo())
        .toList().toString();

    throw new RegraNegocioException(
        "Tipo de usuario nao configurado no banco para " + perfil.name()
            + ". Registros existentes na tabela tipo_usuario: " + registrosBanco);
  }

  private void salvarTelefoneSeInformado(UsuarioEntity usuario, String telefone) {
    if (telefone == null || telefone.isBlank()) {
      return;
    }

    telefoneRepository.save(TelefoneEntity.builder()
        .id(idGeneratorService.proximoId("telefone", "id_telefone"))
        .usuario(usuario)
        .numero(telefone)
        .build());
  }

  private EnderecoEntity salvarEndereco(String cep, String estado, String cidade, String bairro, String rua,
      String numero, String complemento) {
    return enderecoRepository.save(EnderecoEntity.builder()
        .id(idGeneratorService.proximoId("endereco", "id_endereco"))
        .cep(cep)
        .estado(estado)
        .cidade(cidade)
        .bairro(bairro)
        .rua(rua)
        .numero(numero)
        .complemento(complemento)
        .build());
  }

  private AuthResponseDto criarAuthResponse(UsuarioEntity usuario, PerfilUsuarioType perfil) {
    String token = jwtService.gerarToken(usuario, perfil);
    return new AuthResponseDto(
        token,
        "Bearer",
        jwtService.getExpirationSeconds(),
        usuario.getId(),
        usuario.getNome(),
        usuario.getEmail(),
        perfil);
  }

  private record CodigoRecuperacao(String codigo, OffsetDateTime expiraEm) {
  }
}
