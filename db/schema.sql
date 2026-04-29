-- 1. Criação das Tabelas Independentes (Sem Chaves Estrangeiras)

CREATE TABLE USUARIO (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE DISCO (
    id_disco INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    artista VARCHAR(255) NOT NULL,
    ano_lancamento INT,
    genero VARCHAR(100),
    formato VARCHAR(50),
    gravadora VARCHAR(255),
    imagem_capa TEXT
);

-- 2. Criação das Tabelas com Dependência Direta (Nível 1)

CREATE TABLE COLECAO (
    id_colecao INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE
);

CREATE TABLE AVALIACAO_DISCO (
    id_avaliacao INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    id_disco INT,
    nota INT CHECK (nota >= 0 AND nota <= 5), -- Assumindo uma nota de 0 a 5
    comentario TEXT,
    data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_disco) REFERENCES DISCO(id_disco) ON DELETE CASCADE
);

CREATE TABLE POST (
    id_post INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    id_disco INT,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT,
    data_postagem TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_disco) REFERENCES DISCO(id_disco) ON DELETE CASCADE
);

-- 3. Criação das Tabelas com Dependência Indireta (Nível 2)

CREATE TABLE ITEM_COLECAO (
    id_item_colecao INT PRIMARY KEY AUTO_INCREMENT,
    id_colecao INT,
    id_disco INT,
    estado_conservacao VARCHAR(100),
    data_adicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observacao TEXT,
    FOREIGN KEY (id_colecao) REFERENCES COLECAO(id_colecao) ON DELETE CASCADE,
    FOREIGN KEY (id_disco) REFERENCES DISCO(id_disco) ON DELETE CASCADE
);

CREATE TABLE COMENTARIO (
    id_comentario INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    id_post INT,
    texto TEXT NOT NULL,
    data_comentario TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_post) REFERENCES POST(id_post) ON DELETE CASCADE
);

-- Tabela com Chave Primária Composta (Relacionamento N:M entre USUARIO e POST)
CREATE TABLE CURTIDA_POST (
    id_usuario INT,
    id_post INT,
    data_curtida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_post),
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_post) REFERENCES POST(id_post) ON DELETE CASCADE
);