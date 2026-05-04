// src/main/java/com/seusite/discos/service/dto/DiscogsRelease.java
package com.seusite.discos.service.dto;

import java.util.List;

public class DiscogsRelease {

    private int id;
    private String title;
    private String artist;
    private Integer year;
    private String thumb;
    private String coverImage;
    private List<String> genres;
    private List<String> styles;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getThumb() { return thumb; }
    public void setThumb(String thumb) { this.thumb = thumb; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public List<String> getStyles() { return styles; }
    public void setStyles(List<String> styles) { this.styles = styles; }
}
