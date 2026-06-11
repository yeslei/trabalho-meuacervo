package com.seusite.discos.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper central para padronizar as respostas JSON da API.
 * Toda a borda REST (Servlets) usa estes metodos em vez de montar JSON na mao,
 * garantindo Content-Type, charset e formato de erro consistentes.
 */
public final class JsonUtil {

    private static final TypeAdapter<LocalDateTime> LOCAL_DATE_TIME_ADAPTER = new TypeAdapter<LocalDateTime>() {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) { out.nullValue(); return; }
            out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    };

    // Gson configurado uma unica vez. Serializa nulls para o front saber que o campo existe.
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_ADAPTER)
            .create();

    private JsonUtil() {
        // classe utilitaria, nao instanciavel
    }

    /** Escreve um objeto qualquer como JSON com o status informado. */
    public static void escreverJson(HttpServletResponse response, int status, Object corpo) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(corpo));
    }

    /** Resposta 200 OK com o corpo informado. */
    public static void ok(HttpServletResponse response, Object corpo) throws IOException {
        escreverJson(response, HttpServletResponse.SC_OK, corpo);
    }

    /** Resposta de erro padronizada: { "erro": "<codigo>", "mensagem": "<texto>" } */
    public static void erro(HttpServletResponse response, int status, String codigo, String mensagem) throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("erro", codigo);
        corpo.put("mensagem", mensagem);
        escreverJson(response, status, corpo);
    }

    /**
     * Le o corpo da requisicao (JSON) e converte para a classe informada.
     * Ex.: LoginRequest req = JsonUtil.lerCorpo(request, LoginRequest.class);
     * Retorna null se o corpo estiver vazio ou invalido.
     */
    public static <T> T lerCorpo(HttpServletRequest request, Class<T> classe) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                sb.append(linha);
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        try {
            return GSON.fromJson(sb.toString(), classe);
        } catch (Exception e) {
            return null;
        }
    }

    /** Resposta simples de sucesso: { "sucesso": true, "mensagem": "<texto>" } */
    public static void sucesso(HttpServletResponse response, String mensagem) throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("sucesso", true);
        corpo.put("mensagem", mensagem);
        escreverJson(response, HttpServletResponse.SC_OK, corpo);
    }
}
