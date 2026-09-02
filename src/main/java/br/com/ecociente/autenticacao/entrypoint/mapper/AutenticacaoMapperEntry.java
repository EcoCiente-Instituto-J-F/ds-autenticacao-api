package br.com.ecociente.autenticacao.entrypoint.mapper;

import org.springframework.stereotype.Component;

import br.com.ecociente.autenticacao.core.domain.SessaoAutenticada;
import br.com.ecociente.autenticacao.core.domain.UsuarioCredenciais;
import br.com.ecociente.autenticacao.entrypoint.dto.request.LoginRequestDto;
import br.com.ecociente.autenticacao.entrypoint.dto.response.AuthResponseDto;

@Component 
public class AutenticacaoMapperEntry {
  public UsuarioCredenciais toDomain(LoginRequestDto requestDto){
    return new UsuarioCredenciais(requestDto.email(), requestDto.senha());
  }
  
  public AuthResponseDto toResponse(SessaoAutenticada sessao){
    return new AuthResponseDto(
      sessao.getToken(),
      sessao.getTipoToken(),
      sessao.getExpiraEm().longValue(),
      sessao.getUsuarioId(),
      sessao.getNome(),
      sessao.getEmail(),
      sessao.getPerfil()
    );
  }
}
