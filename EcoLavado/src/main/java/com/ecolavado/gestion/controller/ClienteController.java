/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.controller;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.dao.ClienteDAO;
import com.ecolavado.gestion.dao.ClienteDAOImpl;
import com.ecolavado.gestion.model.Cliente;
import com.ecolavado.gestion.util.SecurityUtils; // Para manejar encriptación de datos sensibles
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private ClienteDAO clienteDAO;

    // Inyección de dependencia a través del constructor (Principio de Inversión de Dependencias)
    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }
    
    // Si no se inyecta, se usa la implementación por defecto
    public ClienteController() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public boolean registrarCliente(String nombre, String apellido, String telefono, String direccion, String email, String preferencias) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setEmail(email);
            // Encriptar preferencias antes de guardar (si es necesario)
            cliente.setPreferencias(SecurityUtils.encrypt(preferencias)); 
            cliente.setFechaRegistro(LocalDateTime.now());
            
            clienteDAO.addCliente(cliente);
            logger.info("Cliente registrado con éxito: {}", cliente.getNombre());
            return true;
        } catch (SQLException e) {
            logger.error("Error al registrar cliente: {}", e.getMessage(), e);
            // Podrías lanzar una excepción personalizada o retornar un código de error
            return false;
        } catch (Exception e) { // Captura de errores de encriptación
             logger.error("Error de seguridad al registrar cliente: {}", e.getMessage(), e);
             return false;
        }
    }

    public Cliente obtenerClientePorId(int idCliente) {
        try {
            Optional<Cliente> clienteOpt = clienteDAO.getClienteById(idCliente);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                // Desencriptar preferencias al cargar (si es necesario)
                cliente.setPreferencias(SecurityUtils.decrypt(cliente.getPreferencias()));
                return cliente;
            }
        } catch (SQLException e) {
            logger.error("Error al obtener cliente por ID {}.", idCliente, e);
        } catch (Exception e) {
             logger.error("Error de seguridad al obtener cliente por ID {}.", idCliente, e);
        }
        return null;
    }
    
    public Cliente obtenerClientePorTelefono(String telefono) {
        try {
            Optional<Cliente> clienteOpt = clienteDAO.getClienteByTelefono(telefono);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                // Desencriptar preferencias al cargar (si es necesario)
                cliente.setPreferencias(SecurityUtils.decrypt(cliente.getPreferencias()));
                return cliente;
            }
        } catch (SQLException e) {
            logger.error("Error al obtener cliente por teléfono {}.", telefono, e);
        } catch (Exception e) {
             logger.error("Error de seguridad al obtener cliente por teléfono {}.", telefono, e);
        }
        return null;
    }

    public boolean actualizarCliente(int idCliente, String nombre, String apellido, String telefono, String direccion, String email, String preferencias) {
        try {
            Optional<Cliente> clienteOpt = clienteDAO.getClienteById(idCliente);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                cliente.setNombre(nombre);
                cliente.setApellido(apellido);
                cliente.setTelefono(telefono);
                cliente.setDireccion(direccion);
                cliente.setEmail(email);
                cliente.setPreferencias(SecurityUtils.encrypt(preferencias));
                clienteDAO.updateCliente(cliente);
                logger.info("Cliente actualizado con éxito: ID {}", idCliente);
                return true;
            } else {
                logger.warn("Intento de actualizar cliente inexistente con ID: {}", idCliente);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar cliente con ID {}.", idCliente, e);
            return false;
        } catch (Exception e) {
             logger.error("Error de seguridad al actualizar cliente con ID {}.", idCliente, e);
             return false;
        }
    }

    public boolean eliminarCliente(int idCliente) {
        try {
            clienteDAO.deleteCliente(idCliente);
            logger.info("Cliente eliminado con éxito: ID {}", idCliente);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar cliente con ID {}: {}.", idCliente, e);
            return false;
        }
    }

    public List<Cliente> obtenerTodosLosClientes() {
        try {
            List<Cliente> clientes = clienteDAO.getAllClientes();
            // Desencriptar preferencias para todos los clientes (si aplica)
            clientes.forEach(c -> {
                try {
                    c.setPreferencias(SecurityUtils.decrypt(c.getPreferencias()));
                } catch (Exception e) {
                    logger.error("Error al desencriptar preferencias para cliente ID {}: {}", c.getIdCliente(), e.getMessage());
                }
            });
            return clientes;
        } catch (SQLException e) {
            logger.error("Error al obtener todos los clientes: {}", e.getMessage(), e);
            return List.of(); // Devuelve una lista vacía
        }
    }
}