package com.seusite.discos.model;

public class Faixa {

    private final String titulo;
    private final String duracao;

    public Faixa(String titulo, String duracao) {
        this.titulo = titulo;
        this.duracao = duracao == null ? "" : duracao;
    }

    public String getTitulo() { return titulo; }
    public String getDuracao() { return duracao; }
}
