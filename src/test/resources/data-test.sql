-- Dados minimos para os testes de integracao.

INSERT INTO tipo_usuario (id_tipo_usuario, nome_tipo, descricao) VALUES
  (1, 'Comum', 'Usuario sem vinculo com condominio'),
  (2, 'Sindico', 'Responsavel por um condominio'),
  (3, 'Cooperativa', 'Cooperativa de reciclagem'),
  (4, 'Morador', 'Usuario vinculado a um condominio');

INSERT INTO endereco (id_endereco, cep, cidade, estado, bairro, rua, numero, complemento) VALUES
  (1, '01001000', 'Sao Paulo', 'SP', 'Centro', 'Rua de Teste', '100', NULL);

INSERT INTO tipo_condominio (id_tipo_condominio, nome_tipo, descricao) VALUES
  (1, 'Residencial', 'Condominio residencial');

INSERT INTO condominio (id_condominio, nome, cnpj, status, id_endereco, id_tipo_condominio) VALUES
  (1, 'Condominio de Teste', '11222333000181', TRUE, 1, 1);

-- Sindico pre-cadastrado, ainda sem senha definida (simula alguem que precisa
-- ativar a conta via /auth/ativar-sindico). O hash abaixo e' so um placeholder,
-- ninguem consegue logar com ele ate ativar a conta de verdade.
INSERT INTO usuario (id_usuario, nome, email, senha_hash, status, id_endereco, id_tipo_usuario) VALUES
  (1, 'Sindico Pre-Cadastrado', 'sindico.pre.cadastrado@teste.com', 'senha-provisoria-nao-utilizavel', TRUE, 1, 2);

INSERT INTO sindico (id_sindico, cpf, id_usuario, id_condominio) VALUES
  (1, '12345678901', 1, 1);
