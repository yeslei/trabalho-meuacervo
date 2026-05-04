package com.seusite.discos.model;

import java.sql.Timestamp;

public class Wishlist {

    private int idUsuario;
    private int idDisco;
    private Timestamp dataAdicao;

    public Wishlist() {
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

    public Timestamp getDataAdicao() {
        return dataAdicao;
    }

    public void setDataAdicao(Timestamp dataAdicao) {
        this.dataAdicao = dataAdicao;
    }
}