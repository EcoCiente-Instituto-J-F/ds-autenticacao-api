# Política de Segurança

A **ds-autenticacao-api** é um microsserviço crítico que gerencia autenticação, tokens JWT e credenciais de usuário da plataforma EcoCiente. Trata dados sensíveis incluindo:

- Credenciais de autenticação (email, senha hashada em BCrypt)
- Tokens JWT com claims de identidade (email, perfil de usuário)
- Sessões de autenticação
- Perfis de múltiplos tipos de usuários (residencial, comercial, sindical)

## Versões Suportadas

Apenas o `main` branch é mantido com atualizações de segurança. Não há suporte a versões antigas.

| Versão | Suportada |
|--------|-----------|
| main   | ✅ Sim    |
| Outras | ❌ Não    |

## Reportando uma Vulnerabilidade

**Não abra uma issue pública, não comente em PRs abertas e não envie a informação por e-mail desencriptado.**

### Processo de Reporte Seguro

1. Acesse [GitHub Security Advisories](https://github.com/EcoCiente-Instituto-J-F/ds-autenticacao-api/security/advisories/new)
2. Clique em "Report a vulnerability"
3. Preencha os detalhes (veja abaixo)
4. Envie o relatório

### Ativar Private Vulnerability Reporting

Certifique-se de que a opção está ativa:
1. Vá para **Settings** → **Code security and analysis**
2. Ative **Private vulnerability reporting**

### O que incluir no relato

- **Título:** resumo breve da vulnerabilidade
- **Descrição:** contexto completo
- **Impacto:** o que um atacante pode conseguir? (ex.: acesso a tokens de outro usuário, execução de código, bypass de autenticação)
- **Passos para reproduzir:** com dados **fictícios** (ex.: `user_teste@example.com`, não dados reais)
- **Commit ou versão testada:** qual branch/tag foram usados para validar
- **Configuração:** variáveis de ambiente, banco de dados
- **Ideia de correção:** sua sugestão (opcional, mas bem-vinda)

**Exemplo:**

```
Título: SQL Injection em busca de usuário por email

Impacto: Acesso a credenciais de qualquer usuário, bypass de autenticação

Passos:
1. POST /auth/login com payload:
   {"email": "admin' OR '1'='1", "senha": "qualquer_senha"}
2. API retorna token válido para admin

Testado em: commit abc1234, Java 19, PostgreSQL 14

Correção proposta: usar parameterized queries (já implementadas, mas faltou em uma query edge case em UserRepository.findByEmail)
```

### O que acontece após o reporte

1. **Confirmação:** você receberá confirmação que recebemos o relatório dentro de 48 horas
2. **Investigação:** equipe investigará, reproduzirá e avaliará o risco
3. **Correção privada:** se confirmado, abriremos um draft de fix em segredo, longe de issues/PRs públicas
4. **Advisory:** quando a correção for publicada, um advisory será criado creditando você (opcionalmente)
5. **Prazo:** pedimos prazo razoável (tipicamente 90 dias) para publicar um fix antes de divulgar publicamente

## Escopo

### Dentro do escopo (relatar como vulnerabilidade)

- Acesso a dados de outro usuário (token, credenciais, perfil)
- Bypass de autenticação ou autorização (forjar JWT, logar como outro usuário)
- Injeção de SQL/Cypher/LDAP/comandos com exposição de dados sensíveis
- Vazamento de dados pessoais em logs, stack traces ou responses da API
- Segredos (chaves JWT, senhas) commitados no repositório
- Headers de teste aceitos fora de `APP_ENV=test` (ex.: debug flags ativas em produção)
- Desserialização insegura de JWTs ou dados de entrada

### Fora do escopo (não relatar como vulnerabilidade)

- Bugs sem impacto de segurança (ex.: typo em mensagem de erro, endpoint não documentado)
- Falhas em serviços de terceiros (Google OAuth, banco de dados externo)
- Credenciais padrão marcadas como `CHANGEME` em arquivos de exemplo (`.env.example`)
- DoS direcionado ao seu próprio deploy (ex.: enviar 1M requisições para si mesmo)
- Informação já pública (ex.: versão do Spring Boot no banner da aplicação)
- Social engineering ou phishing

## Como o sistema protege os dados

| Camada | Onde | O que faz |
|--------|------|----------|
| Autenticação | `SecurityConfig.java` | Configura Spring Security para stateless com JWT; sem sessões em memória |
| Filtro JWT | `JwtAuthenticationFilter.java` | Extrai token do header `Authorization: Bearer <token>` e valida em cada request |
| Geração JWT | `JwtService.java` | Assina com `HS256` usando `JWT_SECRET`, inclui claims (email, perfil), expira em `JWT_EXPIRATION_MS` |
| Senhas | `AutenticadorSpringSecurityAdapter.java` | Usa BCrypt para hash; servidor nunca armazena senha em plaintext |
| Banco de dados | PostgreSQL + Spring Data JPA | Usa prepared statements (JPA previne SQL injection); acesso via usuário com mínimo de permissão |
| Validação | `LoginRequestDto.java`, `ValidationError.java` | Valida email e senha em request; rejeita payloads malformados antes de processar |
| Erros | `ApiExceptionHandler.java` | Logs internos contêm stack trace; responses públicas não expõem detalhes de implementação |

## Checklist para deploy em produção

Antes de fazer deploy, verifique:

- [ ] **`APP_ENV=production`** está definido (não `development` ou `test`)
- [ ] **`JWT_SECRET`** é uma string aleatória com 32+ caracteres, **não** uma frase comum
- [ ] **`JWT_EXPIRATION_MS`** é um valor razoável (ex.: 15 minutos = 900000ms, não dias)
- [ ] **Nenhum segredo** em `application.properties` — todas as credenciais vêm de variáveis de ambiente
- [ ] **Todos os `CHANGEME`** de exemplo foram substituídos por valores reais
- [ ] **`DB_PASSWORD`** é uma senha forte (mínimo 16 caracteres, mix de caracteres)
- [ ] **`DB_URL`** usa `sslmode=require` para conexões TLS ao PostgreSQL
- [ ] **Credenciais do banco** têm role com mínimo de permissão (ex.: não `SUPERUSER`)
- [ ] **Endpoints internos** (ex.: `/metrics`, `/actuator`) estão **desabilitados** ou atrás de firewall (não expostos publicamente)
- [ ] **HTTPS/TLS** está ativado no load balancer ou reverse proxy
- [ ] **CORS** está configurado apenas para domínios permitidos (não `*`)
- [ ] **Logs** não incluem senhas, tokens ou IPs de cliente (revisão em `application.properties` e código)
- [ ] **Backups do banco** são criptografados e testados regularmente
- [ ] **Monitoramento** está ativo (alertas para múltiplas tentativas de login falhadas)
- [ ] **Rate limiting** está configurado (ex.: máximo 5 tentativas de login por IP/hora)

## Exemplo de configuração segura em produção

```env
# .env (nunca versionado; apenas no servidor/secret manager)
APP_ENV=production
DB_URL=jdbc:postgresql://db.internal.example.com:5432/ecociente?sslmode=require&search_path=public
DB_USER=app_user_readonly
DB_PASSWORD=aleatoria_super_forte_aqui_32_chars
JWT_SECRET=chave_jwt_aleatoria_e_forte_aqui_32_chars_ou_mais
JWT_EXPIRATION_MS=900000
```

Kubernetes Secret:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: autenticacao-api-secrets
type: Opaque
stringData:
  DB_PASSWORD: aleatoria_super_forte_aqui_32_chars
  JWT_SECRET: chave_jwt_aleatoria_e_forte_aqui_32_chars_ou_mais
```

## Contato e Atribuição

- **E-mail de contato:** ecociente.jef@gmail.com (para dúvidas, mas **não** para relatar vulnerabilidades — use a forma acima)
- **Responsável pela segurança:** Tim de Segurança da EcoCiente
- Relatórios confirmados serão creditados no advisory (com sua permissão)

Obrigado por ajudar a manter a plataforma EcoCiente segura! 🛡️
