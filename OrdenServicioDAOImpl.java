/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.model.OrdenServicio;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrdenServicioDAOImpl implements OrdenServicioDAO {

    private static final Logger logger = LoggerFactory.getLogger(OrdenServicioDAOImpl.class);

    @Override
    public void addOrden(OrdenServicio orden) throws SQLException {
        String sql = "INSERT INTO Ordenes_Servicio (id_orden, id_cliente, fecha_recepcion, costo_total, estado, observaciones, fecha_ultima_actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orden.getIdOrden());
            pstmt.setInt(2, orden.getIdCliente());
            pstmt.setTimestamp(3, Timestamp.valueOf(orden.getFechaRecepcion()));
            pstmt.setBigDecimal(4, orden.getCostoTotal());
            pstmt.setString(5, orden.getEstado());
            pstmt.setString(6, orden.getObservaciones());
            pstmt.setTimestamp(7, Timestamp.valueOf(orden.getFechaUltimaActualizacion()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación de la orden falló, no se insertaron filas.");
            }
            logger.info("Orden de servicio añadida exitosamente: ID {}", orden.getIdOrden());

        } catch (SQLException e) {
            logger.error("Error al añadir orden de servicio: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateOrden(OrdenServicio orden) throws SQLException {
        String sql = "UPDATE Ordenes_Servicio SET id_cliente = ?, fecha_recepcion = ?, costo_total = ?, estado = ?, observaciones = ?, fecha_ultima_actualizacion = ? WHERE id_orden = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orden.getIdCliente());
            pstmt.setTimestamp(2, Timestamp.valueOf(orden.getFechaRecepcion()));
            pstmt.setBigDecimal(3, orden.getCostoTotal());
            pstmt.setString(4, orden.getEstado());
            pstmt.setString(5, orden.getObservaciones());
            pstmt.setTimestamp(6, Timestamp.valueOf(orden.getFechaUltimaActualizacion()));
            pstmt.setString(7, orden.getIdOrden());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró orden con ID {} para actualizar.", orden.getIdOrden());
            } else {
                logger.info("Orden de servicio actualizada exitosamente: ID {}", orden.getIdOrden());
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar orden de servicio con ID {}.", orden.getIdOrden(), e);
            throw e;
        }
    }

    @Override
    public void deleteOrden(String idOrden) throws SQLException {
        String sql = "DELETE FROM Ordenes_Servicio WHERE id_orden = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idOrden);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró orden con ID {} para eliminar.", idOrden);
            } else {
                logger.info("Orden de servicio eliminada exitosamente: ID {}", idOrden);
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar orden de servicio con ID {}.", idOrden, e);
            throw e;
        }
    }

    @Override
    public Optional<OrdenServicio> getOrdenById(String idOrden) throws SQLException {
        String sql = "SELECT * FROM Ordenes_Servicio WHERE id_orden = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idOrden);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrdenServicio(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener orden de servicio por ID {}", idOrden, e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<OrdenServicio> getOrdenesByClienteId(int idCliente) throws SQLException {
        List<OrdenServicio> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM Ordenes_Servicio WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ordenes.add(mapResultSetToOrdenServicio(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener órdenes por ID de cliente {}.", idCliente, e);
            throw e;
        }
        return ordenes;
    }
    
    @Override
    public List<OrdenServicio> getOrdenesByEstado(String estado) throws SQLException {
        List<OrdenServicio> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM Ordenes_Servicio WHERE estado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ordenes.add(mapResultSetToOrdenServicio(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener órdenes por estado {}.", estado, e);
            throw e;
        }
        return ordenes;
    }

    @Override
    public List<OrdenServicio> getAllOrdenes() throws SQLException {
        List<OrdenServicio> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM Ordenes_Servicio";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ordenes.add(mapResultSetToOrdenServicio(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todas las órdenes de servicio: {}", e.getMessage(), e);
            throw e;
        }
        return ordenes;
    }

    private OrdenServicio mapResultSetToOrdenServicio(ResultSet rs) throws SQLException {
        OrdenServicio orden = new OrdenServicio();
        orden.setIdOrden(rs.getString("id_orden"));
        orden.setIdCliente(rs.getInt("id_cliente"));
        orden.setFechaRecepcion(rs.getTimestamp("fecha_recepcion").toLocalDateTime());
        orden.setCostoTotal(rs.getBigDecimal("costo_total"));
        orden.setEstado(rs.getString("estado"));
        orden.setObservaciones(rs.getString("observaciones"));
        orden.setFechaUltimaActualizacion(rs.getTimestamp("fecha_ultima_actualizacion").toLocalDateTime());
        return orden;
    }
}
