package com.seusite.discos.model;

import java.sql.Timestamp;

public class Colecao {

    private int idColecao;
    private String nome;
    private String descricao;
    private Timestamp dataCriacao;
    private int idUsuario;

    public Colecao() {
    }

    public int getIdColecao() {
        return idColecao;
    }

    public void setIdColecao(int idColecao) {
        this.idColecao = idColecao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Timestamp getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Timestamp dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}