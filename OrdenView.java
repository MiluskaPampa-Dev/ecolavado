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
import com.ecolavado.gestion.dao.ServicioDAOImpl;
import com.ecolavado.gestion.model.Cliente;
import com.ecolavado.gestion.model.DetalleOrden;
import com.ecolavado.gestion.model.OrdenServicio;
import com.ecolavado.gestion.model.Servicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal; // Importa BigDecimal

public class OrdenView extends JFrame {

    private OrdenController ordenController;
    private ClienteController clienteController; // Para buscar clientes
    private ServicioDAOImpl servicioDAO; // Para obtener lista de servicios

    private JTextField txtIdClienteOrden, txtObservaciones, txtUrlFoto;
    private JComboBox<String> cmbServicios;
    private JTextField txtCantidadServicio;
    private JButton btnAgregarServicio, btnQuitarServicio, btnRegistrarOrden, btnActualizarEstado, btnConsultarOrden;
    private JComboBox<String> cmbEstadoOrden;
    private JTextField txtIdOrdenConsulta;
    private JTextArea txtAreaDetallesOrden;
    private JList<String> listFotografias;
    private DefaultListModel<String> listModelFotografias;

    private DefaultTableModel detalleServicioTableModel;
    private JTable detalleServicioTable;
    
    private List<DetalleOrden> detallesTemporales; // Para acumular detalles antes de registrar
    private List<String> urlsFotografiasTemporales;

    public OrdenView(OrdenController ordenController, ClienteController clienteController) {
        this.ordenController = ordenController;
        this.clienteController = clienteController;
        this.servicioDAO = new ServicioDAOImpl(); // Inicializar DAO de Servicio
        this.detallesTemporales = new ArrayList<>();
        this.urlsFotografiasTemporales = new ArrayList<>();

        setTitle("Gestión de Órdenes - EcoLavado");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        initComponents();
        loadServicios();
        addListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel Superior: Registro de Órdenes
        JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Nueva Orden de Servicio"));

        txtIdClienteOrden = new JTextField(10);
        txtObservaciones = new JTextField(30);
        txtUrlFoto = new JTextField(30);
        cmbServicios = new JComboBox<>();
        txtCantidadServicio = new JTextField("1", 5);
        btnAgregarServicio = new JButton("Agregar Servicio a Orden");
        btnQuitarServicio = new JButton("Quitar Servicio Seleccionado");
        btnRegistrarOrden = new JButton("Registrar Orden Completa");
        
        listModelFotografias = new DefaultListModel<>();
        listFotografias = new JList<>(listModelFotografias);
        JButton btnAddFoto = new JButton("Agregar Foto URL");
        JButton btnRemoveFoto = new JButton("Quitar Foto Seleccionada");


        topPanel.add(new JLabel("ID Cliente:"));
        topPanel.add(txtIdClienteOrden);
        topPanel.add(new JLabel("Observaciones:"));
        topPanel.add(txtObservaciones);

        topPanel.add(new JLabel("Servicio:"));
        topPanel.add(cmbServicios);
        topPanel.add(new JLabel("Cantidad:"));
        topPanel.add(txtCantidadServicio);
        topPanel.add(btnAgregarServicio);
        topPanel.add(btnQuitarServicio);

        // Tabla de detalles de servicios para la nueva orden
        String[] detalleColumnNames = {"ID Servicio", "Tipo", "Cantidad", "Subtotal"};
        detalleServicioTableModel = new DefaultTableModel(detalleColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        detalleServicioTable = new JTable(detalleServicioTableModel);
        JScrollPane detalleScrollPane = new JScrollPane(detalleServicioTable);
        
        topPanel.add(new JLabel("Fotos (URL):"));
        JPanel fotoPanel = new JPanel(new BorderLayout());
        fotoPanel.add(txtUrlFoto, BorderLayout.CENTER);
        JPanel fotoButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fotoButtonsPanel.add(btnAddFoto);
        fotoButtonsPanel.add(btnRemoveFoto);
        fotoPanel.add(fotoButtonsPanel, BorderLayout.EAST);
        topPanel.add(fotoPanel);
        topPanel.add(new JScrollPane(listFotografias));

        add(topPanel, BorderLayout.NORTH);
        add(detalleScrollPane, BorderLayout.CENTER);
        
        JPanel bottomRegPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRegPanel.add(btnRegistrarOrden);
        add(bottomRegPanel, BorderLayout.SOUTH);

        // Panel Derecho: Consulta y Actualización de Órdenes
        JPanel rightPanel = new JPanel(new BorderLayout(5,5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Consultar y Actualizar Orden"));
        
        JPanel consultaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtIdOrdenConsulta = new JTextField(15);
        btnConsultarOrden = new JButton("Consultar Orden");
        consultaPanel.add(new JLabel("ID Orden:"));
        consultaPanel.add(txtIdOrdenConsulta);
        consultaPanel.add(btnConsultarOrden);
        
        JPanel updateStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbEstadoOrden = new JComboBox<>(new String[]{"PENDIENTE", "EN_PROCESO", "LISTO_PARA_RECOGER", "ENTREGADO", "CANCELADO"});
        btnActualizarEstado = new JButton("Actualizar Estado");
        updateStatusPanel.add(new JLabel("Nuevo Estado:"));
        updateStatusPanel.add(cmbEstadoOrden);
        updateStatusPanel.add(btnActualizarEstado);

        txtAreaDetallesOrden = new JTextArea(10, 30);
        txtAreaDetallesOrden.setEditable(false);
        JScrollPane detallesOrdenScrollPane = new JScrollPane(txtAreaDetallesOrden);

        rightPanel.add(consultaPanel, BorderLayout.NORTH);
        rightPanel.add(updateStatusPanel, BorderLayout.CENTER);
        rightPanel.add(detallesOrdenScrollPane, BorderLayout.SOUTH);
        
        add(rightPanel, BorderLayout.EAST); // Ajustar el layout para el panel derecho
    }

    private void addListeners() {
        btnAgregarServicio.addActionListener(e -> agregarServicioAOrden());
        btnQuitarServicio.addActionListener(e -> quitarServicioDeOrden());
        btnRegistrarOrden.addActionListener(e -> registrarNuevaOrden());
        //btnAddFoto.addActionListener(e -> addFotoUrl());
        //btnRemoveFoto.addActionListener(e -> removeFotoUrl());

        btnConsultarOrden.addActionListener(e -> consultarEstadoOrden());
        btnActualizarEstado.addActionListener(e -> actualizarEstadoOrden());
    }

    private void loadServicios() {
        cmbServicios.removeAllItems();
        try {
            List<Servicio> servicios = servicioDAO.getAllServicios();
            for (Servicio servicio : servicios) {
                cmbServicios.addItem(servicio.getIdServicio() + " - " + servicio.getTipo() + " (S/" + servicio.getPrecioBase() + ")");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar servicios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarServicioAOrden() {
        String selectedServiceString = (String) cmbServicios.getSelectedItem();
        if (selectedServiceString == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un servicio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idServicio = Integer.parseInt(selectedServiceString.split(" - ")[0]);
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidadServicio.getText());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida. Debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Optional<Servicio> servicioOpt = servicioDAO.getServicioById(idServicio);
            if (servicioOpt.isPresent()) {
                Servicio servicio = servicioOpt.get();
                DetalleOrden detalle = new DetalleOrden();
                detalle.setIdServicio(idServicio);
                detalle.setCantidad(cantidad);
                detalle.calcularSubtotal(servicio.getPrecioBase()); // Calcular subtotal
                detalle.setServicio(servicio); // Asociar el objeto Servicio temporalmente

                detallesTemporales.add(detalle);
                updateDetalleTable();
            } else {
                JOptionPane.showMessageDialog(this, "Servicio no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar servicio: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarServicioDeOrden() {
        int selectedRow = detalleServicioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un servicio de la tabla para quitar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        detallesTemporales.remove(selectedRow);
        updateDetalleTable();
    }
    
    private void addFotoUrl() {
        String url = txtUrlFoto.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa una URL de fotografía.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        urlsFotografiasTemporales.add(url);
        listModelFotografias.addElement(url);
        txtUrlFoto.setText("");
    }
    
    private void removeFotoUrl() {
        int selectedIndex = listFotografias.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una URL de fotografía para quitar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        urlsFotografiasTemporales.remove(selectedIndex);
        listModelFotografias.remove(selectedIndex);
    }


    private void updateDetalleTable() {
        detalleServicioTableModel.setRowCount(0); // Limpiar tabla
        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (DetalleOrden detalle : detallesTemporales) {
            detalleServicioTableModel.addRow(new Object[]{
                detalle.getIdServicio(),
                detalle.getServicio().getTipo(), // Asumiendo que el objeto Servicio está asociado
                detalle.getCantidad(),
                detalle.getSubtotal()
            });
            totalCalculado = totalCalculado.add(detalle.getSubtotal());
        }
        // Mostrar costo total en algún lugar si se desea (ej. una JLabel)
        setTitle("Gestión de Órdenes - EcoLavado (Total: S/ " + totalCalculado + ")");
    }

    private void registrarNuevaOrden() {
        String idClienteText = txtIdClienteOrden.getText().trim();
        if (idClienteText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el ID del cliente.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idCliente;
        try {
            idCliente = Integer.parseInt(idClienteText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID de Cliente inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (detallesTemporales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega al menos un servicio a la orden.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String observaciones = txtObservaciones.getText();
        
        // El controller maneja la lógica de validación del cliente y registro
        OrdenServicio nuevaOrden = ordenController.registrarOrden(idCliente, detallesTemporales, observaciones, urlsFotografiasTemporales);

        if (nuevaOrden != null) {
            JOptionPane.showMessageDialog(this, "Orden registrada con éxito. ID: " + nuevaOrden.getIdOrden(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la orden. Verifica los datos y el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultarEstadoOrden() {
        String idOrden = txtIdOrdenConsulta.getText().trim();
        if (idOrden.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID de Orden para consultar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Optional<OrdenServicio> ordenOpt = ordenController.consultarEstadoOrden(idOrden);
        if (ordenOpt.isPresent()) {
            OrdenServicio orden = ordenOpt.get();
            StringBuilder sb = new StringBuilder();
            sb.append("Detalles de la Orden #").append(orden.getIdOrden()).append("\n");
            sb.append("--------------------------------------------------\n");
            sb.append("ID Cliente: ").append(orden.getIdCliente()).append("\n");
            sb.append("Fecha Recepción: ").append(orden.getFechaRecepcion()).append("\n");
            sb.append("Costo Total: S/ ").append(orden.getCostoTotal()).append("\n");
            sb.append("Estado Actual: ").append(orden.getEstado()).append("\n");
            sb.append("Última Actualización: ").append(orden.getFechaUltimaActualizacion()).append("\n");
            sb.append("Observaciones: ").append(orden.getObservaciones() != null ? orden.getObservaciones() : "N/A").append("\n\n");
            
            // Aquí podrías cargar y mostrar detalles de los servicios y fotos si los DAOs los retornaran junto con la orden
            sb.append("Servicios Incluidos (No Cargado desde Controller):\n");
            // sb.append(orden.getDetalles().stream().map(d -> " - " + d.getServicio().getTipo() + " x" + d.getCantidad()).collect(Collectors.joining("\n")));
            
            txtAreaDetallesOrden.setText(sb.toString());
            cmbEstadoOrden.setSelectedItem(orden.getEstado()); // Establecer estado actual en el combo
        } else {
            JOptionPane.showMessageDialog(this, "Orden no encontrada.", "Consulta", JOptionPane.INFORMATION_MESSAGE);
            txtAreaDetallesOrden.setText("");
        }
    }

    private void actualizarEstadoOrden() {
        String idOrden = txtIdOrdenConsulta.getText().trim();
        String nuevoEstado = (String) cmbEstadoOrden.getSelectedItem();
        
        if (idOrden.isEmpty() || nuevoEstado == null) {
            JOptionPane.showMessageDialog(this, "Ingresa el ID de la orden y selecciona un estado.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean success = ordenController.actualizarEstadoOrden(idOrden, nuevoEstado);
        if (success) {
            JOptionPane.showMessageDialog(this, "Estado de la orden actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            consultarEstadoOrden(); // Volver a cargar los detalles actualizados
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar estado de la orden.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtIdClienteOrden.setText("");
        txtObservaciones.setText("");
        txtUrlFoto.setText("");
        txtCantidadServicio.setText("1");
        detallesTemporales.clear();
        urlsFotografiasTemporales.clear();
        listModelFotografias.clear();
        updateDetalleTable();
        setTitle("Gestión de Órdenes - EcoLavado");
    }
}