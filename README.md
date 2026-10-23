# ds-autenticacao-api

![Java](https://img.shields.io/badge/Java-19-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/EcoCiente-Instituto-J-F/ds-autenticacao-api?style=for-the-badge)
![GitHub repo size](https://img.shields.io/github/repo-size/EcoCiente-Instituto-J-F/ds-autenticacao-api?style=for-the-badge)

> API responsável por autenticação, login, cadastro, recuperação de senha, ativação do síndico e geração de JWT dos usuários da plataforma EcoCiente.

## Sobre o projeto

A **ds-autenticacao-api** é um microsserviço de autenticação que centraliza toda a lógica de controle de acesso e identidade da plataforma EcoCiente. Gerencia múltiplos perfis de usuário:

- Usuário comum
- Usuário residencial
- Usuário comercial/industrial
- Síndico (residencial ou comercial/industrial) pré-cadastrado
- Cooperativa

A API utiliza **JWT** (JSON Web Token) para autenticação sem estado, **BCrypt** para segurança de senhas e **PostgreSQL** como banco de dados persistente.

## Pré-requisitos

- **Java 19** ou superior
- **PostgreSQL 12+**
- **Gradle 8+** (incluído no projeto via Gradle Wrapper)
- **Docker** (opcional, para execução containerizada)

## Instalando

### Localmente

1. Clone o repositório:
```bash
git clone https://github.com/EcoCiente-Instituto-J-F/ds-autenticacao-api.git
cd ds-autenticacao-api
```

2. Configure o arquivo `.env`:
```bash
cp .env.example .env
```

3. Edite `.env` com suas credenciais:
```env
DB_URL=jdbc:postgresql://localhost:5432/ecociente?sslmode=require
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_jwt_com_pelo_menos_32_caracteres
JWT_EXPIRATION_MS=300000
```

4. Certifique-se de que o PostgreSQL está rodando e com o banco `ecociente` criado.

5. Compile o projeto:
```bash
./gradlew clean build
```

### Com Docker

1. Clone e configure como acima.

2. Inicie com Docker Compose:
```bash
docker-compose up --build
```

A API estará disponível em `http://localhost:9801`.

## Usando

### Iniciar a aplicação

```bash
./gradlew bootRun
```

A API iniciará na porta **9801** e exporá a documentação Swagger em:
```
http://localhost:9801/swagger-ui.html
```

### Endpoints principais

- `POST /auth/login` — Autentica um usuário e retorna JWT
- `POST /auth/register` — Registra um novo usuário
- `POST /auth/register/cooperativa` — Registra uma cooperativa
- `POST /auth/ativar-sindico` — Ativa um síndico pré-cadastrado
- `POST /auth/esqueci-senha` — Inicia recuperação de senha
- `POST /auth/resetar-senha` — Redefine a senha com token válido

### Exemplos de uso

**Login:**
```bash
curl -X POST http://localhost:9801/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@example.com","senha":"senha123"}'
```

A resposta inclui:
- `token` — JWT para usar em requisições subsequentes
- `perfil` — Tipo de perfil do usuário
- `usuarioId` — Identificador único

**Usar token em requisições autenticadas:**
```bash
curl -X GET http://localhost:9801/seu-endpoint \
  -H "Authorization: Bearer SEU_JWT_TOKEN"
```

## Testes

### Executar testes

```bash
./gradlew test
```

### Executar build com testes (CI/CD)

```bash
./gradlew clean build
```

A suite de testes inclui:
- **Testes unitários:** Lógica de serviços, mappers, validações
- **Testes de integração:** APIs com banco de dados real (Testcontainers + PostgreSQL)
- **Testes de segurança:** Validação de JWT, filtros de autenticação, autorização

Os testes utilizam:
- **JUnit 5** para execução
- **Testcontainers** para PostgreSQL em contêiner isolado
- **Spring Security Test** para contextos autenticados
- **Spring Test** para MockMvc

## Estrutura do projeto

```
src/main/java/br/com/ecociente/autenticacao/
├── AutenticacaoApplication.java              # Classe principal Spring Boot
├── config/security/
│   ├── SecurityConfig.java                   # Configuração de segurança Spring
│   ├── JwtService.java                       # Geração e validação de JWT
│   ├── JwtAuthenticationFilter.java          # Filtro stateless de autenticação
│   ├── AutenticadorSpringSecurityAdapter.java # Adaptador de segurança
│   └── UsuarioDetailsService.java            # Carregamento de usuário para Spring
├── core/
│   ├── domain/                               # Entidades de domínio
│   │   ├── Usuario.java
│   │   ├── Autenticacao.java
│   │   ├── SessaoAutenticada.java
│   │   ├── PerfilUsuarioType.java
│   │   └── UsuarioCredenciais.java
│   ├── service/                              # Lógica de negócio
│   │   ├── AuthService.java                  # Orquestração de autenticação
│   │   └── UsuarioPerfilService.java
│   ├── gateway/                              # Portas de acesso a dados
│   │   ├── AutenticacaoGateway.java
│   │   ├── UsuarioGateway.java
│   │   ├── AutenticadorCredenciaisPort.java
│   │   └── TokenProvaiderPort.java
│   └── exception/
│       ├── RecursoNaoEncontradoException.java
│       └── RegraNegocioException.java
├── dataprovider/                             # Implementações JPA
│   ├── entity/                               # Entidades de banco de dados
│   │   ├── UsuarioEntity.java
│   │   ├── AutenticacaoEntity.java
│   │   └── TipoUsuarioEntity.java
│   ├── repository/                           # Interfaces Spring Data JPA
│   │   ├── UsuarioRepository.java
│   │   ├── AutenticacaoRepository.java
│   │   └── TipoUsuarioRepository.java
│   ├── gateway/                              # Implementações das portas
│   │   ├── UsuarioGatewayImpl.java
│   │   └── AutenticacaoGatewayImpl.java
│   └── mapper/                               # Conversão entity ↔ domain
│       ├── UsuarioMapper.java
│       └── AutenticacaoMapper.java
├── entrypoint/                               # Controllers e DTOs
│   ├── controller/
│   │   └── AuthController.java               # Endpoints REST
│   ├── dto/
│   │   ├── request/
│   │   │   └── LoginRequestDto.java
│   │   ├── response/
│   │   │   └── AuthResponseDto.java
│   │   ├── ErrorResponse.java
│   │   └── ValidationError.java
│   ├── exception/
│   │   └── ApiExceptionHandler.java          # Tratamento centralizado de erros
│   └── mapper/
│       └── AutenticacaoMapperEntry.java

src/test/java/br/com/ecociente/autenticacao/
├── AbstractIntegrationTest.java              # Base para testes de integração
├── AuthControllerIntegrationTest.java        # Testes da API
├── AuthControllerTest.java                   # Testes unitários do controller
├── AutenticacaoApplicationTests.java         # Context load test
└── core/service/
    ├── AuthServiceTest.java
    └── UsuarioPerfilServiceTest.java

src/test/resources/
├── application-test.properties               # Configuração de teste
├── data-test.sql                             # Dados de teste
└── schema-test.sql                           # Schema do banco de teste
```

## Contribuindo

Leia o [CONTRIBUTING.md](CONTRIBUTING.md) para entender como contribuir com o projeto, incluindo configuração do ambiente, convenções de código e processo de pull request.

Ao participar, você concorda em seguir nosso [Código de Conduta](CODE_OF_CONDUCT.md).

## Segurança

Encontrou uma vulnerabilidade de segurança? **Não abra uma issue pública.** Siga o [SECURITY.md](SECURITY.md) para relatar de forma responsável.

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
