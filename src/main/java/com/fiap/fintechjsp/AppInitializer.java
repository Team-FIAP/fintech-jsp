package com.fiap.fintechjsp;

import com.fiap.fintechjsp.utils.MigrationRunner;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        try {
            System.out.println("Iniciando migrations...");
            MigrationRunner.runMigrations(sce.getServletContext());
            System.out.println("Migrations executadas com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao iniciar migrations: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
