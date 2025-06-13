/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.main;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.view.MainView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcoLavadoApp {

    private static final Logger logger = LoggerFactory.getLogger(EcoLavadoApp.class);

    public static void main(String[] args) {
        logger.info("Iniciando aplicación EcoLavado...");
        
        // Ejecutar la interfaz gráfica en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Configuración opcional del look and feel de Swing
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                logger.warn("No se pudo establecer el Look and Feel del sistema: {}", e.getMessage());
            }
            MainView mainView = new MainView();
            mainView.setVisible(true);
            logger.info("Interfaz de usuario principal iniciada.");
        });
    }
}