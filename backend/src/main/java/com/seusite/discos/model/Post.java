package com.seusite.discos.model;

import java.time.LocalDateTime;

public class Post {

    private int idPost;
    private int idUsuario;
    private int idDisco;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataPostagem;
    private String nomeUsuario;
    private String tituloDisco;
    private String artistaDisco;

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdDisco() {
        return idDisco;
    }

    public void setIdDisco(int idDisco) {
        this.idDisco = idDisco;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataPostagem() {
        return dataPostagem;
    }

    public void setDataPostagem(LocalDateTime dataPostagem) {
        this.dataPostagem = dataPostagem;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getTituloDisco() {
        return tituloDisco;
    }

    public void setTituloDisco(String tituloDisco) {
        this.tituloDisco = tituloDisco;
    }

    public String getArtistaDisco() {
        return artistaDisco;
    }

    public void setArtistaDisco(String artistaDisco) {
        this.artistaDisco = artistaDisco;
    }
}
