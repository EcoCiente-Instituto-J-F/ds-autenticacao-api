-- Dados exclusivos do Postgres Testcontainers
TRUNCATE TABLE tb_autenticacoes_api,tb_usuarios,tb_lkp_tipos_usuarios,tb_enderecos RESTART IDENTITY CASCADE;
INSERT INTO tb_enderecos(cep,estado,cidade,logradouro,numero) VALUES ('01001000','SP','Sao Paulo','Rua de Teste','100');
INSERT INTO tb_lkp_tipos_usuarios(nome_tipo,descricao) VALUES ('Comum','Usuario comum'),('Morador','Morador'),('Sindico','Sindico'),('Cooperativa','Cooperativa');
