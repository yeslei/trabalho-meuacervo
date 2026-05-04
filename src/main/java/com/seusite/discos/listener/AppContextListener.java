package com.seusite.discos.listener;

import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.db.DatabaseInitializer;
import com.seusite.discos.service.DiscogsService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Inicializa o banco na subida do Tomcat.
 * Se o DISCOGS_TOKEN estiver configurado e o banco estiver vazio,
 * popula o catálogo inicial com álbuns clássicos em background.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final String[] TERMOS_SEED = {
            "the beatles", "michael jackson", "led zeppelin",
            "miles davis", "pink floyd"
    };

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[MeuAcervo] Inicializando banco de dados...");
        try {
            DatabaseInitializer.init();
        } catch (Exception e) {
            System.err.println("[MeuAcervo] Falha ao inicializar o banco:");
            e.printStackTrace();
        }

        if (System.getenv("DISCOGS_TOKEN") != null) {
            Thread seedThread = new Thread(this::seedDiscogs, "discogs-seed");
            seedThread.setDaemon(true);
            seedThread.start();
        }
    }

    private void seedDiscogs() {
        try {
            DiscoDAO discoDAO = new DiscoDAO();
            if (!discoDAO.listarRecentes(1).isEmpty()) return;

            System.out.println("[MeuAcervo] Banco vazio — populando catálogo via Discogs...");
            DiscogsService svc = new DiscogsService();
            for (String termo : TERMOS_SEED) {
                svc.buscarOuImportar(termo, 6);
                Thread.sleep(600); // respeita rate-limit da API
            }
            System.out.println("[MeuAcervo] Catálogo inicial carregado.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[MeuAcervo] Seed do Discogs falhou: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[MeuAcervo] Aplicação encerrada.");
    }
}
