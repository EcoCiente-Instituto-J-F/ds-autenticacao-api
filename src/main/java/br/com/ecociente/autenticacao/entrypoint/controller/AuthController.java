package br.com.ecociente.autenticacao.entrypoint.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecociente.autenticacao.core.service.AuthService;
import br.com.ecociente.autenticacao.entrypoint.dto.request.AtivacaoSindicoRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.CadastroCooperativaRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.CadastroUsuarioRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.LoginRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.RecuperacaoSenhaRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.request.ResetarSenhaRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.response.AuthResponseDto;
import br.com.ecociente.autenticacao.entrypoint.dto.response.MensagemResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> cadastrarUsuario(@RequestBody @Valid CadastroUsuarioRequestDto request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrarUsuario(request));
  }

  @PostMapping("/register/cooperativa")
  public ResponseEntity<AuthResponseDto> cadastrarCooperativa(@RequestBody @Valid CadastroCooperativaRequestDto request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrarCooperativa(request));
  }

  @PostMapping("/ativar-sindico")
  public ResponseEntity<AuthResponseDto> ativarSindico(@RequestBody @Valid AtivacaoSindicoRequestDto request) {
    return ResponseEntity.ok(authService.ativarSindico(request));
  }

  @PostMapping("/esqueci-senha")
  public ResponseEntity<MensagemResponseDto> solicitarRecuperacaoSenha(@RequestBody @Valid RecuperacaoSenhaRequestDto request) {
    return ResponseEntity.ok(authService.solicitarRecuperacaoSenha(request));
  }

  @PostMapping("/resetar-senha")
  public ResponseEntity<MensagemResponseDto> resetarSenha(@RequestBody @Valid ResetarSenhaRequestDto request) {
    return ResponseEntity.ok(authService.resetarSenha(request));
  }
}
