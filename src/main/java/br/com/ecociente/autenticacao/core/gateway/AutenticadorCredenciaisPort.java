package br.com.ecociente.autenticacao.core.gateway;

public interface AutenticadorCredenciaisPort {
  void autenticar(String email, String senha);
}
