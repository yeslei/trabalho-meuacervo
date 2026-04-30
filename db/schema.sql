CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    username VARCHAR(15) UNIQUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE disco (
    id_disco SERIAL PRIMARY KEY,
    discogs_id INT UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    artista VARCHAR(200),
    ano_lancamento INT,
    genero VARCHAR(100),
    formato VARCHAR(80),
    imagem_capa TEXT
);

CREATE TABLE colecao (
    id_colecao SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,

    CONSTRAINT fk_colecao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
);

CREATE TABLE wishlist (
    id_usuario INT NOT NULL,
    id_disco INT NOT NULL,
    data_adicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_wishlist
        PRIMARY KEY (id_usuario, id_disco),

    CONSTRAINT fk_wishlist_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_wishlist_disco
        FOREIGN KEY (id_disco)
        REFERENCES disco(id_disco)
        ON DELETE CASCADE
);

CREATE TABLE item_colecao (
    id_item_colecao SERIAL PRIMARY KEY,
    id_colecao INT NOT NULL,
    id_disco INT NOT NULL,
    estado_conservacao VARCHAR(80),
    data_adicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observacao TEXT,

    CONSTRAINT fk_item_colecao_colecao
        FOREIGN KEY (id_colecao)
        REFERENCES colecao(id_colecao)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_colecao_disco
        FOREIGN KEY (id_disco)
        REFERENCES disco(id_disco)
        ON DELETE CASCADE,

    CONSTRAINT uk_item_colecao_unico
        UNIQUE (id_colecao, id_disco)
);

CREATE TABLE post (
    id_post SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_disco INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    conteudo TEXT NOT NULL,
    data_postagem TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_post_disco
        FOREIGN KEY (id_disco)
        REFERENCES disco(id_disco)
        ON DELETE CASCADE
);

CREATE TABLE avaliacao_disco (
    id_avaliacao SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_disco INT NOT NULL,
    nota INT NOT NULL CHECK (nota BETWEEN 1 AND 5),
    comentario TEXT,
    data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_avaliacao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_avaliacao_disco
        FOREIGN KEY (id_disco)
        REFERENCES disco(id_disco)
        ON DELETE CASCADE,

    CONSTRAINT uk_usuario_avalia_disco
        UNIQUE (id_usuario, id_disco)
);

CREATE TABLE curtida_post (
    id_usuario INT NOT NULL,
    id_post INT NOT NULL,
    data_curtida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_curtida_post
        PRIMARY KEY (id_usuario, id_post),

    CONSTRAINT fk_curtida_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_curtida_post
        FOREIGN KEY (id_post)
        REFERENCES post(id_post)
        ON DELETE CASCADE
);