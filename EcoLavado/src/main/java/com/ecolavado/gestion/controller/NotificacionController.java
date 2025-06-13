/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.controller;

/**
 *
 * @author Marilu
 */
import com.ecolavado.gestion.dao.ClienteDAO;
import com.ecolavado.gestion.dao.NotificacionDAO;
import com.ecolavado.gestion.dao.OrdenServicioDAO;
import com.ecolavado.gestion.model.Cliente;
import com.ecolavado.gestion.model.Notificacion;
import com.ecolavado.gestion.model.OrdenServicio;
import com.ecolavado.gestion.util.WhatsAppAPI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificacionController {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionController.class);
    
    private NotificacionDAO notificacionDAO;
    private ClienteDAO clienteDAO;
    private OrdenServicioDAO ordenServicioDAO;
    private WhatsAppAPI whatsAppAPI;

    public NotificacionController(NotificacionDAO notificacionDAO, ClienteDAO clienteDAO, OrdenServicioDAO ordenServicioDAO, WhatsAppAPI whatsAppAPI) {
        this.notificacionDAO = notificacionDAO;
        this.clienteDAO = clienteDAO;
        this.ordenServicioDAO = ordenServicioDAO;
        this.whatsAppAPI = whatsAppAPI;
    }

    /**
     * Envía una notificación WhatsApp y la registra en la DB.
     * @param idCliente ID del cliente.
     * @param idOrden ID de la orden (puede ser null).
     * @param tipo Tipo de notificación.
     * @param mensaje Mensaje a enviar.
     * @return true si la operación se completó (no necesariamente si el envío fue exitoso).
     */
    public boolean enviarYRegistrarNotificacion(int idCliente, String idOrden, String tipo, String mensaje) {
        try {
            Optional<Cliente> clienteOpt = clienteDAO.getClienteById(idCliente);
            if (clienteOpt.isEmpty()) {
                logger.warn("No se puede enviar notificación: Cliente con ID {} no encontrado.", idCliente);
                return false;
            }
            Cliente cliente = clienteOpt.get();

            Notificacion notificacion = new Notificacion();
            notificacion.setIdCliente(idCliente);
            notificacion.setIdOrden(idOrden);
            notificacion.setTipo(tipo);
            notificacion.setMensaje(mensaje);
            notificacion.setFechaEnvio(LocalDateTime.now());
            notificacion.setEstadoEnvio("PENDIENTE"); // Se actualiza a ENVIADO/FALLIDO después del intento de envío

            notificacionDAO.addNotificacion(notificacion); // Primero guardar en la DB

            // Intentar enviar vía WhatsApp API
            boolean enviado = whatsAppAPI.enviarMensaje(cliente.getTelefono(), mensaje, notificacion);
            
            // Actualizar el estado de la notificación en la DB
            if (enviado) {
                notificacion.setEstadoEnvio("ENVIADO");
                logger.info("Notificación para cliente ID {} (Orden: {}) enviada con éxito.", idCliente, idOrden);
            } else {
                notificacion.setEstadoEnvio("FALLIDO");
                logger.warn("Fallo al enviar notificación para cliente ID {} (Orden: {}).", idCliente, idOrden);
            }
            notificacionDAO.updateNotificacion(notificacion); // Actualizar el estado

            return true;
        } catch (SQLException e) {
            logger.error("Error SQL al enviar y registrar notificación: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al enviar y registrar notificación: {}", e.getMessage(), e);
            return false;
        }
    }
    
    // Método para reintentar el envío de notificaciones fallidas
    public boolean reintentarNotificacionesFallidas() {
        try {
            List<Notificacion> notificacionesFallidas = notificacionDAO.getNotificacionesByEstado("FALLIDO");
            boolean allReattempted = true;
            for (Notificacion notificacion : notificacionesFallidas) {
                Optional<Cliente> clienteOpt = clienteDAO.getClienteById(notificacion.getIdCliente());
                if (clienteOpt.isPresent()) {
                    Cliente cliente = clienteOpt.get();
                    logger.info("Reintentando notificación ID {} para cliente {}...", notificacion.getIdNotificacion(), cliente.getNombre());
                    boolean reintentado = whatsAppAPI.enviarMensaje(cliente.getTelefono(), notificacion.getMensaje(), notificacion);
                    if (reintentado) {
                        notificacion.setEstadoEnvio("ENVIADO");
                        notificacionDAO.updateNotificacion(notificacion);
                    } else {
                        allReattempted = false; // Al menos una falló de nuevo
                    }
                } else {
                    logger.warn("Cliente para notificación ID {} no encontrado, no se puede reintentar.", notificacion.getIdNotificacion());
                }
            }
            return allReattempted;
        } catch (SQLException e) {
            logger.error("Error SQL al reintentar notificaciones fallidas: {}", e.getMessage(), e);
            return false;
        }
    }
}
