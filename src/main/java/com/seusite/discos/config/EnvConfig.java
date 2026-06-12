package com.seusite.discos.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EnvConfig {

    private static final Map<String, String> DOTENV = carregarDotenv();

    private EnvConfig() {
    }

    public static String get(String nome, String padrao) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            valor = System.getProperty(nome);
        }
        if (valor == null || valor.isBlank()) {
            valor = DOTENV.get(nome);
        }
        return valor == null || valor.isBlank() ? padrao : valor.trim();
    }

    public static String obrigatoria(String nome) {
        String valor = get(nome, null);
        if (valor == null) {
            throw new IllegalStateException("Variavel de ambiente obrigatoria ausente: " + nome);
        }
        return valor;
    }

    private static Map<String, String> carregarDotenv() {
        Map<String, String> valores = new ConcurrentHashMap<>();
        Path arquivo = Path.of(".env");
        if (!Files.exists(arquivo)) {
            return valores;
        }

        try {
            for (String linha : Files.readAllLines(arquivo)) {
                String texto = linha.trim();
                if (texto.isEmpty() || texto.startsWith("#") || !texto.contains("=")) {
                    continue;
                }
                int separador = texto.indexOf('=');
                String chave = texto.substring(0, separador).trim();
                String valor = texto.substring(separador + 1).trim();
                if (!chave.isEmpty()) {
                    valores.put(chave, removerAspas(valor));
                }
            }
        } catch (IOException ignored) {
            // Em deploy, as variaveis do ambiente do host continuam sendo a fonte principal.
        }
        return valores;
    }

    private static String removerAspas(String valor) {
        if ((valor.startsWith("\"") && valor.endsWith("\"")) || (valor.startsWith("'") && valor.endsWith("'"))) {
            return valor.substring(1, valor.length() - 1);
        }
        return valor;
    }
}
