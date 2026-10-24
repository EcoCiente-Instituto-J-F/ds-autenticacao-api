# Política de Segurança

O ds-autenticacao-api é o serviço de login, cadastro, recuperação de senha, ativação de síndico e emissão de JWT do EcoCiente: trata e-mails, senhas (armazenadas como hash) e dados cadastrais dos usuários.

## Versões suportadas

| Versão | Suporte |
| --- | --- |
| `main` (mais recente) | Sim |
| Demais branches | Não |

## Reportando uma vulnerabilidade

**Não abra issue, Pull Request ou discussão pública** para relatar vulnerabilidades.

Use o relato privado do GitHub: <https://github.com/EcoCiente-Instituto-J-F/ds-autenticacao-api/security/advisories/new> (Security → Advisories → Report a vulnerability).

> Mantenedores: ative *Private vulnerability reporting* em Settings → Code security.

Inclua no relato:

- o impacto da falha;
- os passos para reproduzir, usando **dados fictícios**;
- o commit testado e a configuração;
- se possível, uma ideia de correção.

Depois do relato: confirmaremos o recebimento, corrigiremos de forma privada e publicaremos um advisory com crédito a quem reportou, se desejado. Pedimos um prazo razoável para a correção antes de qualquer divulgação pública.

## Escopo

**Dentro do escopo**

- Burla de autenticação ou emissão indevida de JWT
- Escalação de perfil no cadastro (por exemplo, virar síndico ou cooperativa sem pré-cadastro)
- Abuso de `/auth/ativar-sindico`, `/auth/esqueci-senha` e `/auth/resetar-senha` (tomada de conta, enumeração de contas, força bruta)
- Injeção SQL/JPQL
- Vazamento de senhas, hashes, tokens, `JWT_SECRET` ou credenciais de banco em logs, respostas ou repositório
- Segredos commitados e falhas nos workflows que exponham segredos

**Fora do escopo**

- Bugs sem impacto de segurança
- Falhas em serviços de terceiros (PostgreSQL gerenciado, GitHub)
- Valores de exemplo do `.env.example` e do `k8s/secret.example.yaml`
- Negação de serviço no próprio deploy

## Como o sistema protege os dados

| Camada | Onde | O que faz |
| --- | --- | --- |
| Senhas | `config/security/SecurityConfig.java` | `BCryptPasswordEncoder` para o hash das senhas |
| Autenticação | `config/security/JwtService.java`, `JwtAuthenticationFilter.java` | Emite e valida JWT assinado; sessão stateless |
| Autorização | `config/security/SecurityConfig.java` | Só `/auth/**`, Swagger e `/actuator/health` são públicos; o restante exige autenticação |
| Erros | `entrypoint/exception/ApiExceptionHandler.java` | Respostas de erro padronizadas |

Observação: não encontrei limite de tentativas ou de requisições nas rotas públicas de `/auth`; trate isso na borda (gateway/ingress) ou no código.

## Checklist de deploy

- `JWT_SECRET` com pelo menos 32 caracteres e `DB_PASSWORD` em Secrets do Kubernetes (`k8s/secret.example.yaml`), nunca em ConfigMap, imagem ou repositório.
- `DDL_AUTO=validate` em produção: o `k8s/configmap.yaml` e o padrão do `application.properties` usam `update`.
- Definir um `JWT_EXPIRATION_MS` adequado (o padrão é 300000, ou seja, 5 minutos).
- Aplicar limite de requisições em `/auth/login`, `/auth/esqueci-senha` e `/auth/resetar-senha`.
- Avaliar restringir o Swagger em produção; servir somente via HTTPS.
