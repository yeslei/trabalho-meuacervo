package com.seusite.discos.config;

import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {
    private static ApiConfig instance;
    private String apiUrl;
    private String apiToken;

    private ApiConfig() {
        // 1. Tenta buscar primeiro nas variáveis de ambiente (Padrão 12-Factor)
        this.apiUrl = System.getenv("DISCOGS_API_URL");
        this.apiToken = System.getenv("DISCOGS_API_TOKEN");

        // 2. Faz o fallback para o arquivo .properties se as variáveis de ambiente não existirem
        if (this.apiUrl == null || this.apiToken == null || this.apiUrl.isEmpty() || this.apiToken.isEmpty()) {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input != null) {
                    Properties prop = new Properties();
                    prop.load(input);
                    if (this.apiUrl == null || this.apiUrl.isEmpty()) {
                        this.apiUrl = prop.getProperty("discogs.api.url", "https://api.discogs.com");
                    }
                    if (this.apiToken == null || this.apiToken.isEmpty()) {
                        this.apiToken = prop.getProperty("discogs.api.token");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static synchronized ApiConfig getInstance() {
        if (instance == null) {
            instance = new ApiConfig();
        }
        return instance;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiToken() {
        return apiToken;
    }
}