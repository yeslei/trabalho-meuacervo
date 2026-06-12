package com.seusite.discos.controller;

import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** GET /imagem?url=... - proxy seguro para capas externas do Discogs. */
@WebServlet("/imagem")
public class ImagemProxyServlet extends HttpServlet {

    private static final Set<String> HOSTS_PERMITIDOS = Set.of(
            "api-img.discogs.com",
            "i.discogs.com",
            "img.discogs.com",
            "st.discogs.com"
    );

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String urlParam = request.getParameter("url");
        if (urlParam == null || urlParam.isBlank()) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "url-ausente", "Informe a URL da imagem.");
            return;
        }

        URI uri;
        try {
            uri = URI.create(urlParam);
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "url-invalida", "URL da imagem invalida.");
            return;
        }

        if (!urlPermitida(uri)) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "url-nao-permitida", "URL de imagem nao permitida.");
            return;
        }

        try {
            HttpRequest imageRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent", "MeuAcervoApp/1.0")
                    .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                    .GET()
                    .build();

            HttpResponse<byte[]> imageResponse = CLIENT.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray());
            if (imageResponse.statusCode() < 200 || imageResponse.statusCode() >= 300) {
                JsonUtil.erro(response, HttpServletResponse.SC_BAD_GATEWAY,
                        "imagem-indisponivel", "Nao foi possivel carregar a imagem.");
                return;
            }

            String contentType = imageResponse.headers().firstValue("content-type").orElse("image/jpeg");
            if (!contentType.toLowerCase().startsWith("image/")) {
                JsonUtil.erro(response, HttpServletResponse.SC_BAD_GATEWAY,
                        "conteudo-invalido", "A URL informada nao retornou uma imagem.");
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(contentType);
            response.setHeader("Cache-Control", "public, max-age=86400");
            response.getOutputStream().write(imageResponse.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            JsonUtil.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "imagem-interrompida", "A requisicao da imagem foi interrompida.");
        } catch (Exception e) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_GATEWAY,
                    "imagem-indisponivel", "Nao foi possivel carregar a imagem.");
        }
    }

    private boolean urlPermitida(URI uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        return "https".equalsIgnoreCase(scheme)
                && host != null
                && HOSTS_PERMITIDOS.contains(host.toLowerCase());
    }
}
