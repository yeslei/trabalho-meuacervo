// src/main/java/com/seusite/discos/model/Faixa.java
package com.seusite.discos.model;

public class Faixa {

    private int idFaixa;
    private int idDisco;
    private String numero;
    private String titulo;
    private String duracao;
    private int ordem;

    public int getIdFaixa() { return idFaixa; }
    public void setIdFaixa(int idFaixa) { this.idFaixa = idFaixa; }

    public int getIdDisco() { return idDisco; }
    public void setIdDisco(int idDisco) { this.idDisco = idDisco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDuracao() { return duracao; }
    public void setDuracao(String duracao) { this.duracao = duracao; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }
}
