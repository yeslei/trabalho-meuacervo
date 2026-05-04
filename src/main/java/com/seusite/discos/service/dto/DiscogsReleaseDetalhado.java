// src/main/java/com/seusite/discos/service/dto/DiscogsReleaseDetalhado.java
package com.seusite.discos.service.dto;

import java.util.List;

public class DiscogsReleaseDetalhado extends DiscogsRelease {

    private List<DiscogsTrack> tracklist;
    private String formato;

    public List<DiscogsTrack> getTracklist() { return tracklist; }
    public void setTracklist(List<DiscogsTrack> tracklist) { this.tracklist = tracklist; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
}
