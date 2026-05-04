package com.seusite.discos.model;

public class EstatisticaDisco {

    private final double media;
    private final int total;

    public EstatisticaDisco(double media, int total) {
        this.media = media;
        this.total = total;
    }

    public double getMedia() { return media; }
    public int getTotal() { return total; }
    public int getMediaArredondada() { return (int) Math.round(media); }
}
