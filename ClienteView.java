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
import com.ecolavado.gestion.model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClienteView extends JFrame {

    private ClienteController clienteController;

    private JTextField txtNombre, txtApellido, txtTelefono, txtDireccion, txtEmail, txtPreferencias, txtIdBusqueda;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnBuscar, btnMostrarTodos;
    private JTable clienteTable;
    private DefaultTableModel tableModel;

    public ClienteView(ClienteController clienteController) {
        this.clienteController = clienteController;
        setTitle("Gestión de Clientes - EcoLavado");
        setSize(800, 700);
        setLocationRelativeTo(null);
        initComponents();
        addListeners();
        loadAllClientes(); // Cargar clientes al iniciar la vista
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel de entrada de datos
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        
        txtNombre = new JTextField(20);
        txtApellido = new JTextField(20);
        txtTelefono = new JTextField(20);
        txtDireccion = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPreferencias = new JTextField(20);
        txtIdBusqueda = new JTextField(10); // Para buscar por ID o Teléfono

        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(txtNombre);
        inputPanel.add(new JLabel("Apellido:"));
        inputPanel.add(txtApellido);
        inputPanel.add(new JLabel("Teléfono:"));
        inputPanel.add(txtTelefono);
        inputPanel.add(new JLabel("Dirección:"));
        inputPanel.add(txtDireccion);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(txtEmail);
        inputPanel.add(new JLabel("Preferencias:"));
        inputPanel.add(txtPreferencias);
        
        add(inputPanel, BorderLayout.NORTH);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar (ID/Teléfono)");
        btnMostrarTodos = new JButton("Mostrar Todos");
        
        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(new JLabel("Buscar ID/Teléfono:"));
        buttonPanel.add(txtIdBusqueda);
        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnMostrarTodos);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // Tabla de clientes
        String[] columnNames = {"ID", "Nombre", "Apellido", "Teléfono", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa en la tabla
            }
        };
        clienteTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clienteTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        btnRegistrar.addActionListener(e -> registrarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnBuscar.addActionListener(e -> buscarCliente());
        btnMostrarTodos.addActionListener(e -> loadAllClientes());
        
        // Listener para seleccionar fila en la tabla
        clienteTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && clienteTable.getSelectedRow() != -1) {
                int selectedRow = clienteTable.getSelectedRow();
                try {
                    int idCliente = (int) tableModel.getValueAt(selectedRow, 0);
                    Cliente cliente = clienteController.obtenerClientePorId(idCliente);
                    if (cliente != null) {
                        fillForm(cliente);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al cargar datos del cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void registrarCliente() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String telefono = txtTelefono.getText();
        String direccion = txtDireccion.getText();
        String email = txtEmail.getText();
        String preferencias = txtPreferencias.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Apellido y Teléfono son campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = clienteController.registrarCliente(nombre, apellido, telefono, direccion, email, preferencias);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cliente registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadAllClientes();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCliente() {
        int selectedRow = clienteTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla para actualizar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCliente = (int) tableModel.getValueAt(selectedRow, 0);
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String telefono = txtTelefono.getText(); // El teléfono debería ser editable si se permite
        String direccion = txtDireccion.getText();
        String email = txtEmail.getText();
        String preferencias = txtPreferencias.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Apellido y Teléfono son campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = clienteController.actualizarCliente(idCliente, nombre, apellido, telefono, direccion, email, preferencias);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cliente actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadAllClientes();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar cliente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        int selectedRow = clienteTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla para eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCliente = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este cliente?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = clienteController.eliminarCliente(idCliente);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAllClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarCliente() {
        String searchInput = txtIdBusqueda.getText().trim();
        if (searchInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID o número de teléfono para buscar.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = null;
        try {
            // Intentar buscar por ID
            int id = Integer.parseInt(searchInput);
            cliente = clienteController.obtenerClientePorId(id);
        } catch (NumberFormatException e) {
            // Si no es un número, intentar buscar por teléfono
            cliente = clienteController.obtenerClientePorTelefono(searchInput);
        }

        if (cliente != null) {
            tableModel.setRowCount(0); // Limpiar tabla
            tableModel.addRow(new Object[]{
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getEmail()
            });
            fillForm(cliente);
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            tableModel.setRowCount(0); // Limpiar tabla
        }
    }

    private void loadAllClientes() {
        tableModel.setRowCount(0); // Limpiar tabla
        List<Cliente> clientes = clienteController.obtenerTodosLosClientes();
        for (Cliente cliente : clientes) {
            tableModel.addRow(new Object[]{
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getEmail()
            });
        }
    }

    private void fillForm(Cliente cliente) {
        txtNombre.setText(cliente.getNombre());
        txtApellido.setText(cliente.getApellido());
        txtTelefono.setText(cliente.getTelefono());
        txtDireccion.setText(cliente.getDireccion());
        txtEmail.setText(cliente.getEmail());
        txtPreferencias.setText(cliente.getPreferencias()); // Aquí ya está desencriptado
    }

    private void clearForm() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtEmail.setText("");
        txtPreferencias.setText("");
        txtIdBusqueda.setText("");
    }
}