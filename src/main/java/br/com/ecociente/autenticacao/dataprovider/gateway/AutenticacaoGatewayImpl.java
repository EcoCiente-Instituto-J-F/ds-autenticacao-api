package br.com.ecociente.autenticacao.dataprovider.gateway;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import br.com.ecociente.autenticacao.core.domain.Autenticacao;
import br.com.ecociente.autenticacao.core.gateway.AutenticacaoGateway;
import br.com.ecociente.autenticacao.dataprovider.entity.AutenticacaoEntity;
import br.com.ecociente.autenticacao.dataprovider.mapper.AutenticacaoMapper;
import br.com.ecociente.autenticacao.dataprovider.repository.AutenticacaoRepository;
import lombok.AllArgsConstructor;

@Component 
@AllArgsConstructor 
public class AutenticacaoGatewayImpl implements AutenticacaoGateway {
  private final AutenticacaoRepository autenticacaoRepository;
  private final AutenticacaoMapper mapper;

  @Override
  public Autenticacao salvar(Autenticacao autenticacao) {
    AutenticacaoEntity entity = mapper.toEntity(autenticacao);
    entity.setToken(hashToken(entity.getToken()));
    AutenticacaoEntity salvar = autenticacaoRepository.save(entity);
    return mapper.toDomain(salvar);
  }

  private String hashToken(String token){
    try{
      var digest = MessageDigest.getInstance("SHA-256");
      var hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (Exception e) {
      throw new IllegalStateException("Erro ao gerar hash do token", e);
    }
  }
}
