| Metodo | Endpoint                    | Objetivo                                      |
| ------ | --------------------------- | --------------------------------------------- |
| POST   | `/auth/login`               | Autenticar com e-mail e senha e retornar JWT  |
| POST   | `/auth/register`            | Cadastrar usuario comum/residencial/comercial |
| POST   | `/auth/register/cooperativa`| Cadastrar cooperativa                         |
| POST   | `/auth/ativar-sindico`      | Ativar conta de sindico pre-cadastrado        |
| POST   | `/auth/esqueci-senha`       | Solicitar recuperacao de senha                |
| POST   | `/auth/resetar-senha`       | Redefinir senha usando codigo de recuperacao  |
