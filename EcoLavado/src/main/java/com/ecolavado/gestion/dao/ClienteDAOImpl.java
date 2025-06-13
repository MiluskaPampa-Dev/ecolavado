/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.model.Cliente;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClienteDAOImpl implements ClienteDAO {

    private static final Logger logger = LoggerFactory.getLogger(ClienteDAOImpl.class);

    @Override
    public void addCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO Clientes (nombre, apellido, telefono, direccion, email, preferencias, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setString(6, cliente.getPreferencias());
            pstmt.setTimestamp(7, Timestamp.valueOf(cliente.getFechaRegistro()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación del cliente falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación del cliente falló, no se obtuvo ID generado.");
                }
            }
            logger.info("Cliente añadido exitosamente: {}", cliente.getNombre());

        } catch (SQLException e) {
            logger.error("Error al añadir cliente: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE Clientes SET nombre = ?, apellido = ?, telefono = ?, direccion = ?, email = ?, preferencias = ? WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setString(6, cliente.getPreferencias());
            pstmt.setInt(7, cliente.getIdCliente());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró cliente con ID {} para actualizar.", cliente.getIdCliente());
            } else {
                logger.info("Cliente actualizado exitosamente: ID {}", cliente.getIdCliente());
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar cliente con ID {}.", cliente.getIdCliente(), e);
            throw e;
        }
    }

    @Override
    public void deleteCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM Clientes WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No se encontró cliente con ID {} para eliminar.", idCliente);
            } else {
                logger.info("Cliente eliminado exitosamente: ID {}", idCliente);
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar cliente con ID {}.", idCliente, e);
            throw e;
        }
    }

    @Override
    public Optional<Cliente> getClienteById(int idCliente) throws SQLException {
        String sql = "SELECT * FROM Clientes WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener cliente por ID {}.", idCliente, e);
            throw e;
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Cliente> getClienteByTelefono(String telefono) throws SQLException {
        String sql = "SELECT * FROM Clientes WHERE telefono = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, telefono);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener cliente por teléfono {}.", telefono, e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> getAllClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Clientes";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los clientes: {}", e.getMessage(), e);
            throw e;
        }
        return clientes;
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellido(rs.getString("apellido"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setEmail(rs.getString("email"));
        cliente.setPreferencias(rs.getString("preferencias"));
        Timestamp timestamp = rs.getTimestamp("fecha_registro");
        cliente.setFechaRegistro(timestamp != null ? timestamp.toLocalDateTime() : null);
        return cliente;
    }
}
