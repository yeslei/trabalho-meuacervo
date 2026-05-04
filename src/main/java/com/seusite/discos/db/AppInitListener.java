package com.seusite.discos.db;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseInitializer.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nada a fazer
    }
}
