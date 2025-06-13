/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.model.FotografiaOrden;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FotografiaOrdenDAOImpl implements FotografiaOrdenDAO {

    private static final Logger logger = LoggerFactory.getLogger(FotografiaOrdenDAOImpl.class);

    @Override
    public void addFotografiaOrden(FotografiaOrden fotografiaOrden) throws SQLException {
        String sql = "INSERT INTO Fotografias_Orden (id_orden, url_foto, descripcion, fecha_carga) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, fotografiaOrden.getIdOrden());
            pstmt.setString(2, fotografiaOrden.getUrlFoto());
            pstmt.setString(3, fotografiaOrden.getDescripcion());
            pstmt.setTimestamp(4, Timestamp.valueOf(fotografiaOrden.getFechaCarga()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación de la fotografía de orden falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fotografiaOrden.setIdFoto(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación de la fotografía de orden falló, no se obtuvo ID generado.");
                }
            }
            logger.info("Fotografía de orden añadida exitosamente: Orden ID {}, URL {}", fotografiaOrden.getIdOrden(), fotografiaOrden.getUrlFoto());

        } catch (SQLException e) {
            logger.error("Error al añadir fotografía de orden: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateFotografiaOrden(FotografiaOrden fotografiaOrden) throws SQLException {
        String sql = "UPDATE Fotografias_Orden SET id_orden = ?, url_foto = ?, descripcion = ?, fecha_carga = ? WHERE id_foto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fotografiaOrden.getIdOrden());
            pstmt.setString(2, fotografiaOrden.getUrlFoto());
            pstmt.setString(3, fotografiaOrden.getDescripcion());
            pstmt.setTimestamp(4, Timestamp.valueOf(fotografiaOrden.getFechaCarga()));
            pstmt.setInt(5, fotografiaOrden.getIdFoto());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró fotografía de orden con ID {} para actualizar.", fotografiaOrden.getIdFoto());
            } else {
                logger.info("Fotografía de orden actualizada exitosamente: ID {}", fotografiaOrden.getIdFoto());
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar fotografía de orden con ID {}.", fotografiaOrden.getIdFoto(), e);
            throw e;
        }
    }

    @Override
    public void deleteFotografiaOrden(int idFoto) throws SQLException {
        String sql = "DELETE FROM Fotografias_Orden WHERE id_foto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFoto);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró fotografía de orden con ID {} para eliminar.", idFoto);
            } else {
                logger.info("Fotografía de orden eliminada exitosamente: ID {}", idFoto);
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar fotografía de orden con ID {}.", idFoto, e);
            throw e;
        }
    }

    @Override
    public Optional<FotografiaOrden> getFotografiaOrdenById(int idFoto) throws SQLException {
        String sql = "SELECT * FROM Fotografias_Orden WHERE id_foto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFoto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFotografiaOrden(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener fotografía de orden por ID {}.", idFoto, e);
            throw e;
        }
        return Optional.empty();
    }
    
    @Override
    public List<FotografiaOrden> getFotografiasByOrdenId(String idOrden) throws SQLException {
        List<FotografiaOrden> fotos = new ArrayList<>();
        String sql = "SELECT * FROM Fotografias_Orden WHERE id_orden = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idOrden);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fotos.add(mapResultSetToFotografiaOrden(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener fotografías por ID de orden {}.", idOrden, e);
            throw e;
        }
        return fotos;
    }

    @Override
    public List<FotografiaOrden> getAllFotografiasOrden() throws SQLException {
        List<FotografiaOrden> fotografias = new ArrayList<>();
        String sql = "SELECT * FROM Fotografias_Orden";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fotografias.add(mapResultSetToFotografiaOrden(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todas las fotografías de orden: {}", e.getMessage(), e);
            throw e;
        }
        return fotografias;
    }

    private FotografiaOrden mapResultSetToFotografiaOrden(ResultSet rs) throws SQLException {
        FotografiaOrden fotografiaOrden = new FotografiaOrden();
        fotografiaOrden.setIdFoto(rs.getInt("id_foto"));
        fotografiaOrden.setIdOrden(rs.getString("id_orden"));
        fotografiaOrden.setUrlFoto(rs.getString("url_foto"));
        fotografiaOrden.setDescripcion(rs.getString("descripcion"));
        Timestamp timestamp = rs.getTimestamp("fecha_carga");
        fotografiaOrden.setFechaCarga(timestamp != null ? timestamp.toLocalDateTime() : null);
        return fotografiaOrden;
    }
}