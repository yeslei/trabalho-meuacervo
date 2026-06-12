package com.seusite.discos.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seusite.discos.config.ApiConfig;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Faixa;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DiscogsService {

    public static record ResultadoBusca(
            List<Disco> discos,
            int paginaAtual,
            int totalPaginas,
            int totalItens,
            int itensPorPagina
    ) {
    }

    private static final String API_URL = "https://api.discogs.com/database/search";
    private static final String API_URL2 = "https://api.discogs.com";
    private static final String USER_AGENT = "MeuAcervoApp/1.0";

    public List<Disco> buscarDiscosPorTermo(String termo) throws Exception {
        return buscarDiscosPorTermo(termo, 1);
    }

    public List<Disco> buscarDiscosPorTermo(String termo, int pagina) throws Exception {
        return buscarDiscosPorTermoPaginado(termo, pagina).discos();
    }

    public ResultadoBusca buscarDiscosPorTermoPaginado(String termo, int pagina) throws Exception {
        int paginaSolicitada = pagina < 1 ? 1 : pagina;
        List<Disco> listaDiscos = new ArrayList<>();

        String encodedQuery = URLEncoder.encode(termo, StandardCharsets.UTF_8);
        String urlBase = API_URL + "?q=" + encodedQuery + "&type=release"
                + "&page=" + paginaSolicitada + "&per_page=50";

        HttpResponse<String> response = consultarDiscogs(urlBase);

        if (response.statusCode() == 200) {
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray results = jsonObject.getAsJsonArray("results");

            for (JsonElement element : results) {
                JsonObject item = element.getAsJsonObject();
                Disco disco = new Disco();

                if (item.has("id") && !item.get("id").isJsonNull()) {
                    disco.setDiscogsId(item.get("id").getAsInt());
                }

                if (item.has("title") && !item.get("title").isJsonNull()) {
                    String titleFull = item.get("title").getAsString();
                    if (titleFull.contains(" - ")) {
                        String[] partes = titleFull.split(" - ", 2);
                        disco.setArtista(partes[0].trim());
                        disco.setTitulo(partes[1].trim());
                    } else {
                        disco.setTitulo(titleFull);
                        disco.setArtista("Desconhecido");
                    }
                }

                if (item.has("year") && !item.get("year").isJsonNull()) {
                    try {
                        String yearStr = item.get("year").getAsString().substring(0, 4);
                        disco.setAnoLancamento(Integer.parseInt(yearStr));
                    } catch (Exception e) {
                        disco.setAnoLancamento(null);
                    }
                }

                if (item.has("genre") && item.get("genre").isJsonArray()
                        && !item.get("genre").getAsJsonArray().isEmpty()) {
                    disco.setGenero(item.get("genre").getAsJsonArray().get(0).getAsString());
                }

                if (item.has("format") && item.get("format").isJsonArray()
                        && !item.get("format").getAsJsonArray().isEmpty()) {
                    disco.setFormato(item.get("format").getAsJsonArray().get(0).getAsString());
                }

                if (item.has("cover_image") && !item.get("cover_image").isJsonNull()) {
                    disco.setImagemCapa(item.get("cover_image").getAsString());
                }

                listaDiscos.add(disco);
            }

            int paginaAtual = paginaSolicitada;
            int totalPaginas = 1;
            int totalItens = listaDiscos.size();
            int itensPorPagina = listaDiscos.size();
            if (jsonObject.has("pagination") && jsonObject.get("pagination").isJsonObject()) {
                JsonObject pagination = jsonObject.getAsJsonObject("pagination");
                paginaAtual = getIntOrDefault(pagination, "page", paginaSolicitada);
                totalPaginas = Math.max(1, getIntOrDefault(pagination, "pages", 1));
                totalItens = Math.max(0, getIntOrDefault(pagination, "items", totalItens));
                itensPorPagina = Math.max(0, getIntOrDefault(pagination, "per_page", itensPorPagina));
            }

            return new ResultadoBusca(listaDiscos, paginaAtual, totalPaginas, totalItens, itensPorPagina);
        }

        throw new Exception("Erro ao consultar Discogs. Status: " + response.statusCode()
                + ". Corpo: " + resumirCorpo(response.body()));
    }

    private int getIntOrDefault(JsonObject jsonObject, String key, int fallback) {
        if (jsonObject == null || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return jsonObject.get(key).getAsInt();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public List<Faixa> buscarTracklist(int discogsId) throws Exception {
        List<Faixa> tracklist = new ArrayList<>();

        String urlBase = API_URL2 + "/releases/" + discogsId;
        HttpResponse<String> response = consultarDiscogs(urlBase);

        if (response.statusCode() == 200) {
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            if (jsonObject.has("tracklist") && jsonObject.get("tracklist").isJsonArray()) {
                JsonArray tracks = jsonObject.getAsJsonArray("tracklist");
                for (JsonElement trackElement : tracks) {
                    JsonObject trackObj = trackElement.getAsJsonObject();
                    String titulo = trackObj.has("title") && !trackObj.get("title").isJsonNull()
                            ? trackObj.get("title").getAsString() : "";
                    String duracao = trackObj.has("duration") && !trackObj.get("duration").isJsonNull()
                            ? trackObj.get("duration").getAsString() : "";
                    if (!titulo.isBlank()) {
                        tracklist.add(new Faixa(titulo, duracao));
                    }
                }
            }

            return tracklist;
        }

        throw new Exception("Erro ao consultar Discogs para tracklist. Status: " + response.statusCode()
                + ". Corpo: " + resumirCorpo(response.body()));
    }

    private HttpResponse<String> consultarDiscogs(String urlBase) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = enviarRequisicao(client, adicionarToken(urlBase));

        if (tokenConfigurado() && tokenRecusado(response.statusCode())) {
            return enviarRequisicao(client, urlBase);
        }

        return response;
    }

    private HttpResponse<String> enviarRequisicao(HttpClient client, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String adicionarToken(String urlBase) {
        if (!tokenConfigurado()) {
            return urlBase;
        }

        return urlBase + (urlBase.contains("?") ? "&" : "?")
                + "token=" + URLEncoder.encode(ApiConfig.DISCOGS_TOKEN.trim(), StandardCharsets.UTF_8);
    }

    private boolean tokenConfigurado() {
        return ApiConfig.DISCOGS_TOKEN != null && !ApiConfig.DISCOGS_TOKEN.isBlank();
    }

    private boolean tokenRecusado(int statusCode) {
        return statusCode == 401 || statusCode == 403;
    }

    private String resumirCorpo(String body) {
        if (body == null || body.isBlank()) {
            return "sem corpo";
        }

        String texto = body.replaceAll("\\s+", " ").trim();
        return texto.length() > 180 ? texto.substring(0, 180) + "..." : texto;
    }
}
