package br.com.ecociente.autenticacao.config.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import br.com.ecociente.autenticacao.core.gateway.AutenticadorCredenciaisPort;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor 
public class AutenticadorSpringSecurityAdapter implements AutenticadorCredenciaisPort {

  private AuthenticationManager autentica;

  @Override
  public void autenticar(String email, String senha) {
   autentica.authenticate(new UsernamePasswordAuthenticationToken(email, senha));
  }

  
  
}
