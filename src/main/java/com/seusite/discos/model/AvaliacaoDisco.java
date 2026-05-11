package com.seusite.discos.model;

import java.time.LocalDateTime;

public class AvaliacaoDisco {

    private int idAvaliacao;
    private int idUsuario;
    private int idDisco;
    private int nota;
    private String comentario;
    private LocalDateTime dataAvaliacao;

    public int getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(int idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
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

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    // campos desnormalizados: preenchidos nas queries com JOIN
    private String username;
    private Disco disco;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Disco getDisco() { return disco; }
    public void setDisco(Disco disco) { this.disco = disco; }
}
