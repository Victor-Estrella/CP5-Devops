CREATE TABLE funcao (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(30) NOT NULL -- valores: ADMIN, GESTOR, OPERACIONAL, FINANCEIRO, TECNOLOGIA
);

CREATE TABLE usuario (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(120) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    img_perfil VARCHAR(255),
    nome_perfil VARCHAR(255)
);

CREATE TABLE usuario_funcao_tab (
    id_usuario BIGINT NOT NULL,
    id_funcao BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_funcao),
    CONSTRAINT fk_uf_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    CONSTRAINT fk_uf_funcao FOREIGN KEY (id_funcao) REFERENCES funcao(id)
);

CREATE TABLE fornecedor (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(30),
    contato VARCHAR(120),
    email VARCHAR(180),
    telefone VARCHAR(40),
    endereco VARCHAR(255),
    data_cadastro DATE
);

CREATE TABLE produto (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    preco DECIMAL(14,2),
    data_cadastro DATE,
    id_fornecedor BIGINT NOT NULL,
    CONSTRAINT fk_produto_fornecedor FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(id)
);
