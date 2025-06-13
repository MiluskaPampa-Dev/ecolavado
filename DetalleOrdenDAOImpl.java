/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.model.DetalleOrden;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetalleOrdenDAOImpl implements DetalleOrdenDAO {

    private static final Logger logger = LoggerFactory.getLogger(DetalleOrdenDAOImpl.class);

    @Override
    public void addDetalleOrden(DetalleOrden detalleOrden) throws SQLException {
        String sql = "INSERT INTO Detalle_Orden (id_orden, id_servicio, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, detalleOrden.getIdOrden());
            pstmt.setInt(2, detalleOrden.getIdServicio());
            pstmt.setInt(3, detalleOrden.getCantidad());
            pstmt.setBigDecimal(4, detalleOrden.getSubtotal());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación del detalle de orden falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalleOrden.setIdDetalle(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación del detalle de orden falló, no se obtuvo ID generado.");
                }
            }
            logger.info("Detalle de orden añadido exitosamente: Orden ID {}, Servicio ID {}", detalleOrden.getIdOrden(), detalleOrden.getIdServicio());

        } catch (SQLException e) {
            logger.error("Error al añadir detalle de orden: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateDetalleOrden(DetalleOrden detalleOrden) throws SQLException {
        String sql = "UPDATE Detalle_Orden SET id_orden = ?, id_servicio = ?, cantidad = ?, subtotal = ? WHERE id_detalle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, detalleOrden.getIdOrden());
            pstmt.setInt(2, detalleOrden.getIdServicio());
            pstmt.setInt(3, detalleOrden.getCantidad());
            pstmt.setBigDecimal(4, detalleOrden.getSubtotal());
            pstmt.setInt(5, detalleOrden.getIdDetalle());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró detalle de orden con ID {} para actualizar.", detalleOrden.getIdDetalle());
            } else {
                logger.info("Detalle de orden actualizado exitosamente: ID {}", detalleOrden.getIdDetalle());
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar detalle de orden con ID {}.", detalleOrden.getIdDetalle(), e);
            throw e;
        }
    }

    @Override
    public void deleteDetalleOrden(int idDetalle) throws SQLException {
        String sql = "DELETE FROM Detalle_Orden WHERE id_detalle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDetalle);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró detalle de orden con ID {} para eliminar.", idDetalle);
            } else {
                logger.info("Detalle de orden eliminado exitosamente: ID {}", idDetalle);
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar detalle de orden con ID {}.", idDetalle, e);
            throw e;
        }
    }

    @Override
    public Optional<DetalleOrden> getDetalleOrdenById(int idDetalle) throws SQLException {
        String sql = "SELECT * FROM Detalle_Orden WHERE id_detalle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDetalle);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDetalleOrden(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener detalle de orden por ID {}.", idDetalle, e);
            throw e;
        }
        return Optional.empty();
    }
    
    @Override
    public List<DetalleOrden> getDetallesByOrdenId(String idOrden) throws SQLException {
        List<DetalleOrden> detalles = new ArrayList<>();
        String sql = "SELECT * FROM Detalle_Orden WHERE id_orden = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idOrden);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleOrden(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener detalles de orden por ID de orden {}.", idOrden, e);
            throw e;
        }
        return detalles;
    }


    @Override
    public List<DetalleOrden> getAllDetallesOrden() throws SQLException {
        List<DetalleOrden> detalles = new ArrayList<>();
        String sql = "SELECT * FROM Detalle_Orden";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                detalles.add(mapResultSetToDetalleOrden(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los detalles de orden: {}", e.getMessage(), e);
            throw e;
        }
        return detalles;
    }

    private DetalleOrden mapResultSetToDetalleOrden(ResultSet rs) throws SQLException {
        DetalleOrden detalleOrden = new DetalleOrden();
        detalleOrden.setIdDetalle(rs.getInt("id_detalle"));
        detalleOrden.setIdOrden(rs.getString("id_orden"));
        detalleOrden.setIdServicio(rs.getInt("id_servicio"));
        detalleOrden.setCantidad(rs.getInt("cantidad"));
        detalleOrden.setSubtotal(rs.getBigDecimal("subtotal"));
        return detalleOrden;
    }
}