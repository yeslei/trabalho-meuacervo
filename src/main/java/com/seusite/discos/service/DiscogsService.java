package com.seusite.discos.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seusite.discos.model.Disco;
import com.seusite.discos.config.ApiConfig;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// service responsável por interagir com a API do Discogs e transformar os dados em objetos Disco
/**
 * um usuario digit a um termo de busca (ex: "Pink Floyd") e o sistema consulta a API do Discogs, trazendo uma lista de discos relacionados a esse termo. 
 * o servico faz a chamada HTTP, trata a resposta JSON e converter os dados em objetos Java que serao usados na insercao de um disco  que nao esta no banco de dados ainda
 */
public class DiscogsService {


    private static final String TOKEN = "BIocbHggunzxBPNxHQyEWxBEvNdgONLrCuZGOsNt"; 
    private static final String API_URL = "https://api.discogs.com/database/search";
    private static final String API_URL2 = "https://api.discogs.com";
    /**
     * Busca padrão: se não informar a página, traz sempre a primeira (página 1).
     */
    public List<Disco> buscarDiscosPorTermo(String termo) throws Exception {
        return buscarDiscosPorTermo(termo, 1);
    }
    //parâmetro 'pagina' para controlar a paginação da API
    public List<Disco> buscarDiscosPorTermo(String termo, int pagina) throws Exception {
        List<Disco> listaDiscos = new ArrayList<>();

        //prepara a URL com paginação
        String encodedQuery = URLEncoder.encode(termo, StandardCharsets.UTF_8);
        
        //page e per_page na URL
        String urlCompleta = API_URL + "?q=" + encodedQuery + "&type=release" 
                           + "&page=" + pagina + "&per_page=50" 
                           + "&token=" + TOKEN;
        System.out.println("URL COMPLETA: " + urlCompleta);
        // faz a chamada HTTP usando o HttpClient nativo do Java 11+
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlCompleta))
                .header("User-Agent", "MeuAcervoApp/1.0") // O Discogs exige um User-Agent, senão bloqueia!
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verifica se a API respondeu com sucesso (Status 200 OK)
        if (response.statusCode() == 200) {
            
            // Transformação (Parse) do JSON para Objetos Java usando GSON
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray results = jsonObject.getAsJsonArray("results");

            for (JsonElement element : results) {
                JsonObject item = element.getAsJsonObject();
                Disco disco = new Disco();

                // Pegando o ID do Discogs
                if (item.has("id") && !item.get("id").isJsonNull()) {
                    disco.setDiscogsId(item.get("id").getAsInt());
                }

                // O Discogs devolve o título no formato: "Nome do Artista - Nome do Álbum"
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

                // Ano de lançamento
                if (item.has("year") && !item.get("year").isJsonNull()) {
                    try {
                        String yearStr = item.get("year").getAsString().substring(0, 4);
                        disco.setAnoLancamento(Integer.parseInt(yearStr));
                    } catch (Exception e) {
                        disco.setAnoLancamento(null);
                    }
                }

                // Gênero (Vem como um array, pegamos o primeiro)
                if (item.has("genre") && item.get("genre").isJsonArray() && !item.get("genre").getAsJsonArray().isEmpty()) {
                    disco.setGenero(item.get("genre").getAsJsonArray().get(0).getAsString());
                }

                // Formato (Vem como um array, ex: "Vinyl", pegamos o primeiro)
                if (item.has("format") && item.get("format").isJsonArray() && !item.get("format").getAsJsonArray().isEmpty()) {
                    disco.setFormato(item.get("format").getAsJsonArray().get(0).getAsString());
                }

                // Imagem de Capa
                if (item.has("cover_image") && !item.get("cover_image").isJsonNull()) {
                    disco.setImagemCapa(item.get("cover_image").getAsString());
                }

                listaDiscos.add(disco);
            }
        } else {
            throw new Exception("Erro ao consultar Discogs. Status: " + response.statusCode());
        }

        return listaDiscos;
    }

    /**
     * Busca a lista de faixas (tracklist) de um disco específico usando seu ID no Discogs
     */
    public List<String> buscarTracklist(int discogsId) throws Exception {
        List<String> tracklist = new ArrayList<>();
        
        // Endpoint específico de releases: /releases/{id}
        String urlCompleta = API_URL2 + "/releases/" + discogsId + "?token=" + TOKEN;
        System.out.println("URL para tracklist: " + urlCompleta);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlCompleta))
                .header("User-Agent", "MeuAcervoApp/1.0")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            if (jsonObject.has("tracklist") && jsonObject.get("tracklist").isJsonArray()) {
                JsonArray tracks = jsonObject.getAsJsonArray("tracklist");
                for (JsonElement trackElement : tracks) {
                    JsonObject trackObj = trackElement.getAsJsonObject();
                    if (trackObj.has("title") && !trackObj.get("title").isJsonNull()) {
                        tracklist.add(trackObj.get("title").getAsString());
                    }
                }
            }
        } else {
            throw new Exception("Erro ao consultar Discogs para tracklist. Status: " + response.statusCode());
        }
        return tracklist;
    }
}