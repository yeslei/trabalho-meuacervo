// src/main/java/com/seusite/discos/service/DiscogsClient.java
package com.seusite.discos.service;

import com.seusite.discos.service.dto.DiscogsRelease;
import com.seusite.discos.service.dto.DiscogsReleaseDetalhado;
import com.seusite.discos.service.dto.DiscogsTrack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DiscogsClient {

    private static final String BASE_URL = "https://api.discogs.com";

    private final String token;
    private final HttpClient httpClient;

    public DiscogsClient() {
        //String t = System.getenv("DISCOGS_TOKEN");
        String t = EmZWpVMlGiuLPPDLitZGLjEaZgRseLBDmvOAymky
        if (t == null || t.isBlank()) {
            throw new IllegalStateException("DISCOGS_TOKEN não configurado");
        }
        this.token = t;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public List<DiscogsRelease> buscar(String termo) throws Exception {
        return buscar(termo, 25);
    }

    public List<DiscogsRelease> buscar(String termo, int perPage) throws Exception {
        String url = BASE_URL + "/database/search?q="
                + URLEncoder.encode(termo, StandardCharsets.UTF_8)
                + "&type=release&per_page=" + perPage
                + "&token=" + token;
        String body = executarRequisicao(url);
        return parseResultados(body);
    }

    public DiscogsReleaseDetalhado buscarDetalhe(int discogsId) throws Exception {
        String url = BASE_URL + "/releases/" + discogsId + "?token=" + token;
        String body = executarRequisicao(url);
        return parseDetalhe(body);
    }

    private String executarRequisicao(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "MeuAcervo/1.0 +http://localhost")
                .header("Authorization", "Discogs token=" + token)
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 429) {
            Thread.sleep(1500);
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Discogs retornou HTTP " + response.statusCode() + " para: " + url);
        }

        return response.body();
    }

    private List<DiscogsRelease> parseResultados(String json) {
        JSONObject root = new JSONObject(json);
        JSONArray results = root.optJSONArray("results");
        List<DiscogsRelease> lista = new ArrayList<>();
        if (results == null) return lista;

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            DiscogsRelease r = new DiscogsRelease();
            r.setId(item.optInt("id", 0));
            r.setThumb(item.optString("thumb", ""));
            r.setCoverImage(item.optString("cover_image", ""));

            String anoStr = item.optString("year", "");
            r.setYear(anoStr.isEmpty() ? null : parseAno(anoStr));

            // "title" na busca vem como "Artista - Album"
            String fullTitle = item.optString("title", "");
            int sep = fullTitle.indexOf(" - ");
            if (sep > 0) {
                r.setArtist(fullTitle.substring(0, sep).trim());
                r.setTitle(fullTitle.substring(sep + 3).trim());
            } else {
                r.setArtist("");
                r.setTitle(fullTitle);
            }

            JSONArray genres = item.optJSONArray("genre");
            if (genres != null) {
                List<String> gs = new ArrayList<>();
                for (int j = 0; j < genres.length(); j++) gs.add(genres.getString(j));
                r.setGenres(gs);
            }

            JSONArray styles = item.optJSONArray("style");
            if (styles != null) {
                List<String> ss = new ArrayList<>();
                for (int j = 0; j < styles.length(); j++) ss.add(styles.getString(j));
                r.setStyles(ss);
            }

            lista.add(r);
        }
        return lista;
    }

    private DiscogsReleaseDetalhado parseDetalhe(String json) {
        JSONObject root = new JSONObject(json);
        DiscogsReleaseDetalhado d = new DiscogsReleaseDetalhado();

        d.setId(root.optInt("id", 0));
        d.setTitle(root.optString("title", ""));

        int ano = root.optInt("year", 0);
        d.setYear(ano == 0 ? null : ano);

        // artista principal
        JSONArray artists = root.optJSONArray("artists");
        if (artists != null && artists.length() > 0) {
            String nome = artists.getJSONObject(0).optString("name", "");
            // Discogs às vezes adiciona " (2)" no final do nome
            nome = nome.replaceAll("\\s*\\(\\d+\\)$", "").trim();
            d.setArtist(nome);
        }

        // imagens: preferir "primary", senão a primeira
        JSONArray images = root.optJSONArray("images");
        if (images != null && images.length() > 0) {
            String uri = "";
            String uri150 = "";
            for (int i = 0; i < images.length(); i++) {
                JSONObject img = images.getJSONObject(i);
                if ("primary".equals(img.optString("type", "")) || uri.isEmpty()) {
                    uri = img.optString("uri", "");
                    uri150 = img.optString("uri150", uri);
                }
            }
            d.setCoverImage(uri);
            d.setThumb(uri150);
        }

        // gêneros
        JSONArray genres = root.optJSONArray("genres");
        if (genres != null) {
            List<String> gs = new ArrayList<>();
            for (int i = 0; i < genres.length(); i++) gs.add(genres.getString(i));
            d.setGenres(gs);
        }

        // formato: primeiro item de "formats"
        JSONArray formats = root.optJSONArray("formats");
        if (formats != null && formats.length() > 0) {
            d.setFormato(formats.getJSONObject(0).optString("name", ""));
        }

        // tracklist
        JSONArray tracks = root.optJSONArray("tracklist");
        List<DiscogsTrack> faixas = new ArrayList<>();
        if (tracks != null) {
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject t = tracks.getJSONObject(i);
                // ignorar "heading" (separadores de lado de vinil sem duração)
                if ("heading".equals(t.optString("type_", ""))) continue;
                DiscogsTrack faixa = new DiscogsTrack();
                faixa.setPosition(t.optString("position", ""));
                faixa.setTitle(t.optString("title", ""));
                faixa.setDuration(t.optString("duration", ""));
                faixas.add(faixa);
            }
        }
        d.setTracklist(faixas);

        return d;
    }

    private Integer parseAno(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
