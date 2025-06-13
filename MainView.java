/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.view;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.controller.ClienteController;
import com.ecolavado.gestion.controller.OrdenController;
import com.ecolavado.gestion.controller.NotificacionController;
import com.ecolavado.gestion.dao.ClienteDAOImpl;
import com.ecolavado.gestion.dao.DetalleOrdenDAOImpl;
import com.ecolavado.gestion.dao.FotografiaOrdenDAOImpl;
import com.ecolavado.gestion.dao.NotificacionDAOImpl;
import com.ecolavado.gestion.dao.OrdenServicioDAOImpl;
import com.ecolavado.gestion.dao.ServicioDAOImpl;
import com.ecolavado.gestion.model.Cliente;
import com.ecolavado.gestion.model.OrdenServicio;
import com.ecolavado.gestion.util.WhatsAppAPI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Esta sería la ventana principal de la aplicación de escritorio
public class MainView extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(MainView.class);

    private ClienteController clienteController;
    private OrdenController ordenController;
    private NotificacionController notificacionController;

    private JButton btnGestionClientes;
    private JButton btnGestionOrdenes;
    private JButton btnEnviarNotificaciones;
    private JTextArea outputArea; // Para mostrar mensajes

    public MainView() {
        setTitle("EcoLavado - Sistema de Gestión");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana

        // Inicializar controladores con sus DAOs (Inyección manual por simplicidad)
        ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
        ServicioDAOImpl servicioDAO = new ServicioDAOImpl();
        OrdenServicioDAOImpl ordenServicioDAO = new OrdenServicioDAOImpl();
        DetalleOrdenDAOImpl detalleOrdenDAO = new DetalleOrdenDAOImpl();
        FotografiaOrdenDAOImpl fotografiaOrdenDAO = new FotografiaOrdenDAOImpl();
        NotificacionDAOImpl notificacionDAO = new NotificacionDAOImpl();
        WhatsAppAPI whatsAppAPI = new WhatsAppAPI();

        clienteController = new ClienteController(clienteDAO);
        ordenController = new OrdenController(ordenServicioDAO, clienteDAO, servicioDAO, detalleOrdenDAO, notificacionDAO, fotografiaOrdenDAO, whatsAppAPI);
        notificacionController = new NotificacionController(notificacionDAO, clienteDAO, ordenServicioDAO, whatsAppAPI);


        initComponents();
        addListeners();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        btnGestionClientes = new JButton("Gestión de Clientes");
        btnGestionOrdenes = new JButton("Gestión de Órdenes");
        btnEnviarNotificaciones = new JButton("Reintentar Notificaciones Fallidas");
        outputArea = new JTextArea(10, 50);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputArea.setEditable(false);

        panel.add(btnGestionClientes);
        panel.add(btnGestionOrdenes);
        panel.add(btnEnviarNotificaciones);
        panel.add(scrollPane);

        add(panel);
    }

    private void addListeners() {
        btnGestionClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí podrías abrir una nueva ventana o panel para la gestión de clientes
                new ClienteView(clienteController).setVisible(true);
            }
        });

        btnGestionOrdenes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí podrías abrir una nueva ventana o panel para la gestión de órdenes
                new OrdenView(ordenController, clienteController).setVisible(true);
            }
        });
        
        btnEnviarNotificaciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean result = notificacionController.reintentarNotificacionesFallidas();
                if (result) {
                    appendToOutput("Reintento de notificaciones fallidas completado.");
                } else {
                    appendToOutput("Hubo errores al reintentar algunas notificaciones.");
                }
            }
        });
    }

    private void appendToOutput(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}
