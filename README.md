# ds-autenticacao-api

API responsavel por login, cadastro, recuperacao de senha, ativacao do sindico e JWT dos usuarios do EcoCiente.

Perfis atendidos:
- Usuario comum
- Usuario residencial
- Usuario comercial/industrial
- Sindico residencial ou comercial/industrial pre-cadastrado
- Cooperativa

## Configuracao

Variaveis esperadas:
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES` (opcional, padrao 120)

Porta padrao: `9801`.

## Endpoints

- `POST /auth/login`
- `POST /auth/register`
- `POST /auth/register/cooperativa`
- `POST /auth/ativar-sindico`
- `POST /auth/esqueci-senha`
- `POST /auth/resetar-senha`
