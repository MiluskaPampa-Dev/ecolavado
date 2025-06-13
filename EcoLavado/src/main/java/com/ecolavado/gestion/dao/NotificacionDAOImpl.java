/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.model.Notificacion;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificacionDAOImpl implements NotificacionDAO {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionDAOImpl.class);

    @Override
    public void addNotificacion(Notificacion notificacion) throws SQLException {
        String sql = "INSERT INTO Notificaciones (id_cliente, id_orden, tipo, mensaje, fecha_envio, estado_envio) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notificacion.getIdCliente());
            pstmt.setString(2, notificacion.getIdOrden()); // Puede ser NULL
            pstmt.setString(3, notificacion.getTipo());
            pstmt.setString(4, notificacion.getMensaje());
            pstmt.setTimestamp(5, Timestamp.valueOf(notificacion.getFechaEnvio()));
            pstmt.setString(6, notificacion.getEstadoEnvio());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación de la notificación falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notificacion.setIdNotificacion(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación de la notificación falló, no se obtuvo ID generado.");
                }
            }
            logger.info("Notificación añadida exitosamente: ID Cliente {}, Tipo {}", notificacion.getIdCliente(), notificacion.getTipo());

        } catch (SQLException e) {
            logger.error("Error al añadir notificación: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateNotificacion(Notificacion notificacion) throws SQLException {
        String sql = "UPDATE Notificaciones SET id_cliente = ?, id_orden = ?, tipo = ?, mensaje = ?, fecha_envio = ?, estado_envio = ? WHERE id_notificacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificacion.getIdCliente());
            pstmt.setString(2, notificacion.getIdOrden());
            pstmt.setString(3, notificacion.getTipo());
            pstmt.setString(4, notificacion.getMensaje());
            pstmt.setTimestamp(5, Timestamp.valueOf(notificacion.getFechaEnvio()));
            pstmt.setString(6, notificacion.getEstadoEnvio());
            pstmt.setInt(7, notificacion.getIdNotificacion());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró notificación con ID {} para actualizar.", notificacion.getIdNotificacion());
            } else {
                logger.info("Notificación actualizada exitosamente: ID {}", notificacion.getIdNotificacion());
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar notificación con ID {}.", notificacion.getIdNotificacion(), e);
            throw e;
        }
    }

    @Override
    public void deleteNotificacion(int idNotificacion) throws SQLException {
        String sql = "DELETE FROM Notificaciones WHERE id_notificacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNotificacion);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró notificación con ID {} para eliminar.", idNotificacion);
            } else {
                logger.info("Notificación eliminada exitosamente: ID {}", idNotificacion);
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar notificación con ID {}.", idNotificacion, e);
            throw e;
        }
    }

    @Override
    public Optional<Notificacion> getNotificacionById(int idNotificacion) throws SQLException {
        String sql = "SELECT * FROM Notificaciones WHERE id_notificacion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNotificacion);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToNotificacion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener notificación por ID {}.", idNotificacion, e);
            throw e;
        }
        return Optional.empty();
    }
    
    @Override
    public List<Notificacion> getNotificacionesByClienteId(int idCliente) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM Notificaciones WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener notificaciones por ID de cliente {}.", idCliente, e);
            throw e;
        }
        return notificaciones;
    }

    @Override
    public List<Notificacion> getNotificacionesByOrdenId(String idOrden) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM Notificaciones WHERE id_orden = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idOrden);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener notificaciones por ID de orden {}.", idOrden, e);
            throw e;
        }
        return notificaciones;
    }

    @Override
    public List<Notificacion> getNotificacionesByEstado(String estado) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM Notificaciones WHERE estado_envio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener notificaciones por estado {}.", estado, e);
            throw e;
        }
        return notificaciones;
    }

    @Override
    public List<Notificacion> getAllNotificaciones() throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM Notificaciones";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                notificaciones.add(mapResultSetToNotificacion(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todas las notificaciones: {}", e.getMessage(), e);
            throw e;
        }
        return notificaciones;
    }

    private Notificacion mapResultSetToNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdNotificacion(rs.getInt("id_notificacion"));
        notificacion.setIdCliente(rs.getInt("id_cliente"));
        notificacion.setIdOrden(rs.getString("id_orden"));
        notificacion.setTipo(rs.getString("tipo"));
        notificacion.setMensaje(rs.getString("mensaje"));
        Timestamp timestamp = rs.getTimestamp("fecha_envio");
        notificacion.setFechaEnvio(timestamp != null ? timestamp.toLocalDateTime() : null);
        notificacion.setEstadoEnvio(rs.getString("estado_envio"));
        return notificacion;
    }
}