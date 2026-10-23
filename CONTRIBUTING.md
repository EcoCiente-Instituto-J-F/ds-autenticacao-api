# Contribuindo com a ds-autenticacao-api

Obrigado por considerar contribuir! Ao participar, você concorda em seguir o [Código de Conduta](CODE_OF_CONDUCT.md).

Para relatar um problema de segurança, **não** abra uma issue — siga o [SECURITY.md](SECURITY.md).

## Idioma

Todos os commits, PRs e comentários devem estar em **português (Brasil)**.

## Configurando o ambiente

### 1. Faça um fork e clone

```bash
git clone https://github.com/SEU_USUARIO/ds-autenticacao-api.git
cd ds-autenticacao-api
git remote add upstream https://github.com/EcoCiente-Instituto-J-F/ds-autenticacao-api.git
```

### 2. Configure Java e Gradle

Certifique-se de ter **Java 19+** instalado:

```bash
java -version
```

O Gradle Wrapper está incluído no projeto; não precisa instalar Gradle globalmente.

### 3. Configure o banco de dados de desenvolvimento

**Opção A — PostgreSQL local:**

1. Instale PostgreSQL 12+
2. Crie o banco:
```bash
createdb ecociente
```

3. Configure `.env` na raiz do projeto:
```bash
cp .env.example .env
```

4. Edite `.env` com suas credenciais locais:
```env
DB_URL=jdbc:postgresql://localhost:5432/ecociente?sslmode=require
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=dev_secret_minimo_32_caracteres_para_teste
JWT_EXPIRATION_MS=300000
```

**Opção B — Docker Compose (recomendado):**

```bash
docker-compose up -d
```

Será iniciado um PostgreSQL na porta 5432 com credenciais padrão. Ajuste `.env` conforme necessário.

### 4. Compile e valide a configuração

```bash
./gradlew clean build
```

Se tudo passar, seu ambiente está pronto!

## Testando sua mudança

### Executar todos os testes

```bash
./gradlew test
```

### Executar testes de uma classe específica

```bash
./gradlew test --tests AuthControllerIntegrationTest
```

### Executar testes com saída detalhada

```bash
./gradlew test --info
```

### Regras de testes

- **Toda mudança no código deve incluir um teste que falha sem ela** — TDD é incentivado.
- Testes **não devem** fazer requisições a APIs reais, usar dados de produção ou executar contra banco externo.
- Use `Testcontainers` para testes de integração que precisam do PostgreSQL — um contêiner limpo é iniciado para cada suite.
- Dados de teste devem ser fictícios (ex.: `usuario@teste.local`, não dados reais ou de produção).

### Validação local antes de push

```bash
./gradlew clean build
```

Se este comando passar, sua mudança está pronta para PR. O CI rodará a mesma suite.

## Regras do projeto

- **Nenhum segredo no repositório** — tokens JWT, credenciais de banco, chaves privadas nunca são commitadas. Use `.env` ou variáveis de ambiente.
- **Nenhum dado pessoal real em testes** — fixtures usam dados fictícios e anônimos.
- **Configuração centralizada** — variáveis de ambiente são lidas de `application.properties` (produção) e `application-test.properties` (testes); não há configurações espalhadas pelo código.
- **Autenticação stateless** — JWT é extraído do header `Authorization: Bearer <token>` e validado em cada requisição; não há sessões ou cache de usuário em memória entre requisições.
- **Sem dependências novas sem discussão** — antes de adicionar uma dependência ao `build.gradle`, abra uma issue ou discuss em PR para avaliar segurança, tamanho e se está alinhada com a stack.

## Estrutura de commits e branches

### Nomes de branch

Crie branches a partir de `main` com o padrão:

```
feat/nome-descritivo
fix/nome-descritivo
chore/nome-descritivo
refactor/nome-descritivo
test/nome-descritivo
docs/nome-descritivo
```

**Exemplo:**
```bash
git checkout -b feat/adicionar-validacao-email
git checkout -b fix/corrigir-expiracao-jwt
```

### Mensagens de commit

Siga **Conventional Commits**:

```
<tipo>(<escopo>): <descrição curta>

<corpo detalhado se necessário>
```

**Tipos:**
- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `refactor:` Mudança de código sem alterar comportamento
- `test:` Adicionar ou atualizar testes
- `docs:` Adicionar ou atualizar documentação
- `chore:` Tarefas de configuração, dependências, build

**Escopo (opcional):** Parte do sistema (`security`, `auth`, `entity`, etc.)

**Exemplos:**
```
feat(security): adicionar validação de token expirado
fix(jwt): corrigir parsing de claims no JwtService
refactor(mapper): unificar UsuarioMapper e AutenticacaoMapper
test(auth): adicionar testes para login com perfil inválido
docs: atualizar README com exemplos de curl
```

## Pull requests

### Antes de abrir a PR

1. **Rebase na main:**
```bash
git fetch upstream
git rebase upstream/main
```

2. **Rode a suite completa localmente:**
```bash
./gradlew clean build
```

3. **Verifique que os testes passam** e nenhuma flag de aviso é acionada.

### Abrindo a PR

1. Clique em "New Pull Request" no seu fork
2. Defina o base como `main` do repositório oficial
3. Preencha o título e descrição (veja abaixo)
4. Aguarde o CI passar e submeta

### Descrição da PR

Use este template:

```markdown
## Descrição

Que problema isso resolve ou qual funcionalidade adiciona?
(1–2 frases)

## Mudanças

- Ponto 1
- Ponto 2
- Ponto 3

## Testes

- [ ] Adicionei testes que validam a mudança
- [ ] Todos os testes passam (`./gradlew clean build`)
- [ ] Nenhum teste foi removido ou desabilitado

## Checklist de segurança

- [ ] Nenhum segredo, token ou credencial foi commitado
- [ ] Nenhum dado pessoal real está em testes
- [ ] As mudanças não expõem dados sensíveis em logs ou responses
```

### Revisão e merge

- O CI deve passar (build, testes, formatação)
- Um mantenedor revisará a PR e solicitará mudanças ou aprovará
- Nós fazemos **squash** ao mesclar, então sua série de commits se torna um commit na `main` com sua mensagem da PR

## Reportando bugs

Abra uma issue com o template padrão:

- **Título:** resumo breve do problema
- **Descrição:** contexto, como reproduzir (passos), comportamento esperado vs. atual
- **Ambiente:** versão Java, SO, como iniciou a API (local, Docker, K8s)
- **Logs:** output relevante de stderr/stdout (mascare credenciais)

**Exemplo:**
```
Título: Login falha com email contendo caractere '+' (plus addressing)

Descrição:
- Reprodução: registre usuário com email user+tag@example.com, depois tente fazer login
- Esperado: login bem-sucedido
- Atual: 400 Bad Request, mensagem "Email inválido"
- Ambiente: Java 19, PostgreSQL 14, API rodando localmente via `./gradlew bootRun`
```

## Propondo novas funcionalidades

Abra uma issue de **Discussion** antes de implementar:

- **Título:** resumo breve da ideia
- **Descrição:** qual é o caso de uso, por que é importante, como você propõe resolver
- Aguarde feedback de um mantenedor antes de começar o código

**Exemplo:**
```
Título: Adicionar suporte a OAuth2 via Google

Descrição:
Caso de uso: permitir que usuários façam login com conta Google, reduzindo fricção.
Proposta: adicionar endpoint `POST /auth/login/google` que aceita `idToken` do Google,
valida contra Google APIs, e retorna JWT conforme a flow atual.
```

## Perguntas?

- Abra uma [Discussion](https://github.com/EcoCiente-Instituto-J-F/ds-autenticacao-api/discussions) para dúvidas
- Ou comente numa issue relacionada

Obrigado por contribuir com a plataforma EcoCiente! 🌱
