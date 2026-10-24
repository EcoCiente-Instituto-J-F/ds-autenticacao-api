# Contribuindo com o ds-autenticacao-api

Ao participar, você concorda em seguir o [Código de Conduta](CODE_OF_CONDUCT.md). Para reportar um problema de segurança, **não** abra uma issue — siga o [SECURITY.md](SECURITY.md).

## Idioma

Documentação, issues, Pull Requests e mensagens ao usuário em português do Brasil.

## Configurando o ambiente

1. Instale o JDK 19 e o Docker (os testes de integração usam Testcontainers com PostgreSQL).
2. Clone o repositório:
   ```bash
   git clone https://github.com/EcoCiente-Instituto-J-F/ds-autenticacao-api.git
   cd ds-autenticacao-api
   ```
3. Crie um `.env` a partir do `.env.example` (ignorado pelo Git) e ajuste os valores.
4. Execute com `./gradlew bootRun` (Windows: `.\gradlew.bat bootRun`).

## Testando sua mudança

O CI roda:

```bash
./gradlew clean build --no-daemon
```

Toda mudança de comportamento deve vir com teste (JUnit) que falharia sem ela, em `src/test/java`. Os testes não devem depender de rede, APIs pagas ou dados de produção.

## Regras do projeto

- Nenhum segredo (chaves, tokens, senhas) no repositório.
- Nenhum dado pessoal real em testes, fixtures ou exemplos.
- Nenhuma dependência nova sem discussão prévia.
- Mantenha as camadas `config` / `core` / `dataprovider` / `entrypoint`: o `core` depende apenas de gateways (interfaces).
- Novas variáveis lidas em `application.properties` entram também no `.env.example` e na documentação.
- Rotas de autenticação ficam sob `/auth`; qualquer rota nova pública deve ser justificada na PR (`SecurityConfig`).
- Senhas e tokens nunca são logados nem devolvidos em texto puro.

## Pull requests

1. Crie uma branch a partir da `main` (ex.: `feat/nome-da-mudanca`).
2. Faça commits focados com prefixos `feat:`, `fix:`, `chore:`, `docs:`, `test:` ou `refactor:`.
3. Abra a PR para a `main`. O bot do projeto preenche a descrição automaticamente (PRs de forks não recebem esse recurso).
4. O CI precisa passar (quando houver) e um mantenedor revisa antes do merge.

## Reportando bugs e propondo funcionalidades

Abra uma issue com o comportamento esperado, o observado, os passos para reproduzir e a versão. Use dados fictícios. Para vulnerabilidades, siga o [SECURITY.md](SECURITY.md).
