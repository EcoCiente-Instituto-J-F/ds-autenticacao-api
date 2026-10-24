<p align="center">
  <img src="assets/card-readme.png" alt="Card do repositório" width="100%">
</p>

# ds-autenticacao-api

![Java](https://img.shields.io/badge/Java-19-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-JPA-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-wrapper-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-manifests-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![GitHub repo size](https://img.shields.io/github/repo-size/EcoCiente-Instituto-J-F/ds-autenticacao-api?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/EcoCiente-Instituto-J-F/ds-autenticacao-api?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge)


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
- `JWT_EXPIRATION_MS` (opcional, padrao 300000, ou seja, 5 minutos)

Porta padrao: `9801`.

## Endpoints

- `POST /auth/login`
- `POST /auth/register`
- `POST /auth/register/cooperativa`
- `POST /auth/ativar-sindico`
- `POST /auth/esqueci-senha`
- `POST /auth/resetar-senha`

## Contribuindo

Leia o [CONTRIBUTING.md](CONTRIBUTING.md) antes de abrir uma Pull Request. Ao participar, você concorda com o [Código de Conduta](CODE_OF_CONDUCT.md).

## Segurança

Encontrou uma vulnerabilidade? Siga o [SECURITY.md](SECURITY.md). **Não abra issue pública.**

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE).
