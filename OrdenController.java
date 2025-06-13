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
import com.ecolavado.gestion.dao.DetalleOrdenDAO;
import com.ecolavado.gestion.dao.FotografiaOrdenDAO;
import com.ecolavado.gestion.dao.NotificacionDAO;
import com.ecolavado.gestion.dao.OrdenServicioDAO;
import com.ecolavado.gestion.dao.ServicioDAO;
import com.ecolavado.gestion.model.Cliente;
import com.ecolavado.gestion.model.DetalleOrden;
import com.ecolavado.gestion.model.FotografiaOrden;
import com.ecolavado.gestion.model.Notificacion;
import com.ecolavado.gestion.model.OrdenServicio;
import com.ecolavado.gestion.model.Servicio;
import com.ecolavado.gestion.util.WhatsAppAPI;
import com.google.common.base.Strings; // Ejemplo de uso de Guava
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Para generar IDs de orden
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrdenController {

    private static final Logger logger = LoggerFactory.getLogger(OrdenController.class);
    
    private OrdenServicioDAO ordenDAO;
    private ClienteDAO clienteDAO;
    private ServicioDAO servicioDAO;
    private DetalleOrdenDAO detalleOrdenDAO;
    private NotificacionDAO notificacionDAO;
    private FotografiaOrdenDAO fotografiaOrdenDAO;
    private WhatsAppAPI whatsAppAPI;

    // Inyección de dependencias (Principio de Inversión de Dependencias)
    public OrdenController(OrdenServicioDAO ordenDAO, ClienteDAO clienteDAO, ServicioDAO servicioDAO, 
                           DetalleOrdenDAO detalleOrdenDAO, NotificacionDAO notificacionDAO, 
                           FotografiaOrdenDAO fotografiaOrdenDAO, WhatsAppAPI whatsAppAPI) {
        this.ordenDAO = ordenDAO;
        this.clienteDAO = clienteDAO;
        this.servicioDAO = servicioDAO;
        this.detalleOrdenDAO = detalleOrdenDAO;
        this.notificacionDAO = notificacionDAO;
        this.fotografiaOrdenDAO = fotografiaOrdenDAO;
        this.whatsAppAPI = whatsAppAPI;
    }
    
    // Puedes tener un constructor por defecto que inicialice las implementaciones por defecto
    // (Útil para pruebas o si no usas un framework de DI)

    /**
     * Registra una nueva orden de servicio.
     * @param idCliente ID del cliente asociado a la orden.
     * @param serviciosSolicitados Lista de Mapas: [{idServicio: int, cantidad: int}]
     * @param observaciones Observaciones adicionales para la orden.
     * @param urlsFotografias Lista de URLs de fotografías de las prendas.
     * @return La OrdenServicio creada o null si falla.
     */
    public OrdenServicio registrarOrden(int idCliente, List<DetalleOrden> detalles, 
                                        String observaciones, List<String> urlsFotografias) {
        
        try {
            // 1. Validar cliente existente
            Optional<Cliente> clienteOpt = clienteDAO.getClienteById(idCliente);
            if (clienteOpt.isEmpty()) {
                logger.warn("Intento de crear orden para cliente inexistente: ID {}", idCliente);
                return null;
            }
            Cliente cliente = clienteOpt.get();

            // 2. Crear nueva OrdenServicio
            OrdenServicio nuevaOrden = new OrdenServicio();
            nuevaOrden.setIdOrden(UUID.randomUUID().toString()); // Genera un ID único (ej. "a1b2c3d4-e5f6-7890-1234-567890abcdef")
            nuevaOrden.setIdCliente(idCliente);
            nuevaOrden.setFechaRecepcion(LocalDateTime.now());
            nuevaOrden.setEstado("PENDIENTE");
            nuevaOrden.setObservaciones(Strings.isNullOrEmpty(observaciones) ? null : observaciones); // Guava para manejar nulos/vacíos
            nuevaOrden.setFechaUltimaActualizacion(LocalDateTime.now());

            // 3. Calcular costo total y asociar detalles
            BigDecimal costoTotal = BigDecimal.ZERO;
            for (DetalleOrden detalle : detalles) {
                Optional<Servicio> servicioOpt = servicioDAO.getServicioById(detalle.getIdServicio());
                if (servicioOpt.isEmpty()) {
                    logger.warn("Servicio con ID {} no encontrado para la orden.", detalle.getIdServicio());
                    throw new SQLException("Servicio no encontrado."); // O manejar de otra forma
                }
                Servicio servicio = servicioOpt.get();
                detalle.calcularSubtotal(servicio.getPrecioBase());
                detalle.setIdOrden(nuevaOrden.getIdOrden()); // Asignar el ID de la orden
                nuevaOrden.addDetalle(detalle);
                costoTotal = costoTotal.add(detalle.getSubtotal());
            }
            nuevaOrden.setCostoTotal(costoTotal);

            // 4. Guardar la orden y sus detalles en la BD (transaccional)
            // Se necesitaría una transacción aquí para asegurar la atomicidad de la operación.
            // Para simplificar, lo hacemos secuencialmente, pero en un entorno real se usaría Connection.setAutoCommit(false);
            ordenDAO.addOrden(nuevaOrden);
            for (DetalleOrden detalle : nuevaOrden.getDetalles()) {
                detalleOrdenDAO.addDetalleOrden(detalle);
            }

            // 5. Guardar fotografías
            for (String url : urlsFotografias) {
                FotografiaOrden foto = new FotografiaOrden();
                foto.setIdOrden(nuevaOrden.getIdOrden());
                foto.setUrlFoto(url);
                foto.setFechaCarga(LocalDateTime.now());
                fotografiaOrdenDAO.addFotografiaOrden(foto);
                nuevaOrden.addFotografia(foto);
            }

            // 6. Enviar notificación de confirmación
            String mensajeConfirmacion = String.format("¡Hola %s! Tu orden de lavado #%s ha sido recibida con un costo total de %.2f. Estado: %s.",
                                                      cliente.getNombre(), nuevaOrden.getIdOrden(), nuevaOrden.getCostoTotal(), nuevaOrden.getEstado());
            Notificacion notificacionConfirmacion = new Notificacion(0, cliente.getIdCliente(), nuevaOrden.getIdOrden(), 
                                                                     "CONFIRMACION", mensajeConfirmacion, LocalDateTime.now(), "PENDIENTE");
            notificacionDAO.addNotificacion(notificacionConfirmacion);
            
            // Envío asíncrono o posterior para no bloquear el registro de la orden
            whatsAppAPI.enviarMensaje(cliente.getTelefono(), mensajeConfirmacion, notificacionConfirmacion); 
            
            logger.info("Orden #{} registrada para cliente ID {}.", nuevaOrden.getIdOrden(), idCliente);
            return nuevaOrden;

        } catch (SQLException e) {
            logger.error("Error SQL al registrar orden: {}", e.getMessage(), e);
            // Rollback de la transacción si se usara
            return null;
        } catch (Exception e) {
            logger.error("Error inesperado al registrar orden: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Actualiza el estado de una orden y envía notificación al cliente.
     * @param idOrden ID de la orden a actualizar.
     * @param nuevoEstado Nuevo estado de la orden.
     * @return true si la actualización fue exitosa, false de lo contrario.
     */
    public boolean actualizarEstadoOrden(String idOrden, String nuevoEstado) {
        try {
            Optional<OrdenServicio> ordenOpt = ordenDAO.getOrdenById(idOrden);
            if (ordenOpt.isEmpty()) {
                logger.warn("Intento de actualizar estado de orden inexistente: ID {}", idOrden);
                return false;
            }

            OrdenServicio orden = ordenOpt.get();
            String estadoAnterior = orden.getEstado();
            orden.setEstado(nuevoEstado);
            orden.setFechaUltimaActualizacion(LocalDateTime.now());
            ordenDAO.updateOrden(orden);

            // Obtener cliente para la notificación
            Optional<Cliente> clienteOpt = clienteDAO.getClienteById(orden.getIdCliente());
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                String mensajeEstado = String.format("¡Hola %s! El estado de tu orden #%s ha cambiado de %s a %s.",
                                                    cliente.getNombre(), orden.getIdOrden(), estadoAnterior, nuevoEstado);
                Notificacion notificacionEstado = new Notificacion(0, cliente.getIdCliente(), orden.getIdOrden(), 
                                                                   "ESTADO", mensajeEstado, LocalDateTime.now(), "PENDIENTE");
                notificacionDAO.addNotificacion(notificacionEstado);
                whatsAppAPI.enviarMensaje(cliente.getTelefono(), mensajeEstado, notificacionEstado);
                logger.info("Estado de orden #{} actualizado a {} y notificación enviada.", idOrden, nuevoEstado);
            } else {
                logger.warn("Cliente asociado a la orden {} no encontrado para enviar notificación de estado.", idOrden);
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error SQL al actualizar estado de orden {}.", idOrden, e);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar estado de orden {}.", idOrden, e);
            return false;
        }
    }
    
    // Método para obtener el estado de una orden
    public Optional<OrdenServicio> consultarEstadoOrden(String idOrden) {
        try {
            // También se podrían cargar los detalles y fotografías aquí si se necesitan en la vista
            return ordenDAO.getOrdenById(idOrden);
        } catch (SQLException e) {
            logger.error("Error al consultar estado de orden {}.", idOrden, e);
            return Optional.empty();
        }
    }

    // Otros métodos de negocio como generar reportes, gestionar servicios, etc.
}