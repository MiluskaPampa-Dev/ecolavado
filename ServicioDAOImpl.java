/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Marilu
 */
import com.ecolavado.gestion.model.Servicio;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioDAOImpl implements ServicioDAO {

    private static final Logger logger = LoggerFactory.getLogger(ServicioDAOImpl.class);

    @Override
    public void addServicio(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO Servicios (tipo, descripcion, precio_base, tiempo_estimado_min) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, servicio.getTipo());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setInt(4, servicio.getTiempoEstimadoMin());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación del servicio falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servicio.setIdServicio(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación del servicio falló, no se obtuvo ID generado.");
                }
            }
            logger.info("Servicio añadido exitosamente: {}", servicio.getTipo());

        } catch (SQLException e) {
            logger.error("Error al añadir servicio: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateServicio(Servicio servicio) throws SQLException {
        String sql = "UPDATE Servicios SET tipo = ?, descripcion = ?, precio_base = ?, tiempo_estimado_min = ? WHERE id_servicio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, servicio.getTipo());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setInt(4, servicio.getTiempoEstimadoMin());
            pstmt.setInt(5, servicio.getIdServicio());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró servicio con ID {} para actualizar.", servicio.getIdServicio());
            } else {
                logger.info("Servicio actualizado exitosamente: ID {}", servicio.getIdServicio());
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar servicio con ID {}.", servicio.getIdServicio(), e);
            throw e;
        }
    }

    @Override
    public void deleteServicio(int idServicio) throws SQLException {
        String sql = "DELETE FROM Servicios WHERE id_servicio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idServicio);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró servicio con ID {} para eliminar.", idServicio);
            } else {
                logger.info("Servicio eliminado exitosamente: ID {}", idServicio);
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar servicio con ID {}.", idServicio, e);
            throw e;
        }
    }

    @Override
    public Optional<Servicio> getServicioById(int idServicio) throws SQLException {
        String sql = "SELECT * FROM Servicios WHERE id_servicio = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idServicio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToServicio(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener servicio por ID {}", idServicio, e);
            throw e;
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Servicio> getServicioByTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM Servicios WHERE tipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToServicio(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener servicio por tipo {}.", tipo, e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Servicio> getAllServicios() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM Servicios";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                servicios.add(mapResultSetToServicio(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los servicios: {}", e.getMessage(), e);
            throw e;
        }
        return servicios;
    }

    private Servicio mapResultSetToServicio(ResultSet rs) throws SQLException {
        Servicio servicio = new Servicio();
        servicio.setIdServicio(rs.getInt("id_servicio"));
        servicio.setTipo(rs.getString("tipo"));
        servicio.setDescripcion(rs.getString("descripcion"));
        servicio.setPrecioBase(rs.getBigDecimal("precio_base"));
        servicio.setTiempoEstimadoMin(rs.getInt("tiempo_estimado_min"));
        return servicio;
    }
}