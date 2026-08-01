-- Schema usado SOMENTE nos testes de integracao (Testcontainers).
-- E' uma copia fiel do schema real do banco (script_sql_ecociente.sql):
-- chaves primarias INT puro, sem auto-incremento, ja que a aplicacao gera
-- os IDs manualmente (IdGeneratorService).

CREATE TABLE endereco (
	id_endereco INT PRIMARY KEY,
	cep VARCHAR(8) NOT NULL CHECK(LENGTH(cep) = 8),
	cidade VARCHAR(100) NOT NULL,
	estado CHAR(2) NOT NULL CHECK(LENGTH(estado) = 2),
	bairro VARCHAR (100),
	rua VARCHAR(100) NOT NULL,
	numero VARCHAR NOT NULL,
	complemento VARCHAR(100)
);

CREATE TABLE tipo_usuario(
	id_tipo_usuario INT PRIMARY KEY,
	nome_tipo VARCHAR(50) NOT NULL,
	descricao VARCHAR(100)
);

CREATE TABLE tipo_condominio(
	id_tipo_condominio INT PRIMARY KEY,
	nome_tipo VARCHAR(12) NOT NULL,
	descricao VARCHAR(100)
);

CREATE TABLE usuario(
	id_usuario INT PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	email VARCHAR(150) NOT NULL UNIQUE,
	senha_hash VARCHAR(255) NOT NULL,
	data_cadastro DATE NOT NULL DEFAULT CURRENT_DATE,
	status BOOLEAN NOT NULL DEFAULT TRUE,
	id_endereco INT NOT NULL,
	id_tipo_usuario INT NOT NULL,

	CONSTRAINT fk_usuario_endereco FOREIGN KEY(id_endereco) REFERENCES endereco(id_endereco),
	CONSTRAINT fk_usuario_tipo FOREIGN KEY(id_tipo_usuario) REFERENCES tipo_usuario(id_tipo_usuario)
);

CREATE TABLE telefone(
	id_telefone INT PRIMARY KEY,
	numero VARCHAR(11) NOT NULL CHECK(LENGTH(numero) >= 9 AND LENGTH(numero) <= 11),
	id_usuario INT NOT NULL,

	CONSTRAINT fk_telefone_usuario FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE condominio(
	id_condominio INT PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	cnpj VARCHAR(14) NOT NULL UNIQUE CHECK(LENGTH(cnpj) = 14),
	status BOOLEAN NOT NULL DEFAULT TRUE,
	id_endereco INT NOT NULL,
	id_tipo_condominio INT,

	CONSTRAINT fk_condominio_endereco FOREIGN KEY (id_endereco) REFERENCES endereco(id_endereco),
	CONSTRAINT fk_condominio_tipo FOREIGN KEY (id_tipo_condominio) REFERENCES tipo_condominio(id_tipo_condominio)
);

CREATE TABLE sindico(
	id_sindico INT PRIMARY KEY,
	cpf VARCHAR(11) NOT NULL UNIQUE CHECK(LENGTH(cpf) = 11),
	data_inicio_mandato DATE,
	data_fim_mandato DATE,
	id_usuario INT NOT NULL,
	id_condominio INT NOT NULL,

	CONSTRAINT fk_sindico_usuario FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario),
	CONSTRAINT fk_sindico_condominio FOREIGN KEY(id_condominio) REFERENCES condominio(id_condominio)
);

CREATE TABLE cooperativa (
	id_cooperativa INT PRIMARY KEY,
	cnpj VARCHAR(14) NOT NULL UNIQUE CHECK (LENGTH(cnpj) = 14),
	id_usuario INT NOT NULL,
	id_endereco INT NOT NULL,

	CONSTRAINT fk_cooperativa_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
	CONSTRAINT fk_cooperativa_endereco FOREIGN KEY (id_endereco) REFERENCES endereco (id_endereco)
);

CREATE TABLE condominio_cooperativa (
	id_cooperativa_condominio INT PRIMARY KEY,
	data_inicio DATE DEFAULT CURRENT_DATE,
	data_fim DATE,
	id_condominio INT NOT NULL,
	id_cooperativa INT NOT NULL,

	CONSTRAINT fk_cooperativa_condominio FOREIGN KEY (id_condominio) REFERENCES condominio (id_condominio),
	CONSTRAINT fk_condominio_cooperativa FOREIGN KEY (id_cooperativa) REFERENCES cooperativa (id_cooperativa)
);
