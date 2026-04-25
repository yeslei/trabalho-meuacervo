package com.seusite.discos.db;

import com.seusite.discos.config.ConnectionFactory;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Criação das tabelas base (Sem Foreign Keys)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS usuario (
                    id_usuario SERIAL PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    senha VARCHAR(255) NOT NULL,
                    cpf VARCHAR(14) UNIQUE,
                    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS disco (
                    id_disco SERIAL PRIMARY KEY,
                    discogs_id INT UNIQUE,
                    titulo VARCHAR(200) NOT NULL,
                    artista VARCHAR(200),
                    ano_lancamento INT,
                    genero VARCHAR(100),
                    formato VARCHAR(80),
                    gravadora VARCHAR(150),
                    imagem_capa TEXT
                );
            """);

            // 2. Criação das tabelas com dependência de Nível 1
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS colecao (
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
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS avaliacao_disco (
                    id_avaliacao SERIAL PRIMARY KEY,
                    id_usuario INT NOT NULL,
                    id_disco INT NOT NULL,
                    nota INT CHECK (nota >= 0 AND nota <= 5),
                    comentario TEXT,
                    data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_avaliacao_usuario
                        FOREIGN KEY (id_usuario)
                        REFERENCES usuario(id_usuario)
                        ON DELETE CASCADE,
                    CONSTRAINT fk_avaliacao_disco
                        FOREIGN KEY (id_disco)
                        REFERENCES disco(id_disco)
                        ON DELETE CASCADE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS post (
                    id_post SERIAL PRIMARY KEY,
                    id_usuario INT NOT NULL,
                    id_disco INT NOT NULL,
                    titulo VARCHAR(255) NOT NULL,
                    conteudo TEXT,
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
            """);

            // 3. Criação das tabelas com dependência de Nível 2
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS item_colecao (
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
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS comentario (
                    id_comentario SERIAL PRIMARY KEY,
                    id_usuario INT NOT NULL,
                    id_post INT NOT NULL,
                    texto TEXT NOT NULL,
                    data_comentario TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_comentario_usuario
                        FOREIGN KEY (id_usuario)
                        REFERENCES usuario(id_usuario)
                        ON DELETE CASCADE,
                    CONSTRAINT fk_comentario_post
                        FOREIGN KEY (id_post)
                        REFERENCES post(id_post)
                        ON DELETE CASCADE
                );
            """);

            // Tabela associativa (N:M)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS curtida_post (
                    id_usuario INT NOT NULL,
                    id_post INT NOT NULL,
                    data_curtida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
            """);

            System.out.println("Banco de dados inicializado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar o banco de dados:");
            e.printStackTrace();
        }
    }
}