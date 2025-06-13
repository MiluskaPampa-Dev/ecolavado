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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Para manejar casos donde no se encuentra un cliente

public interface ClienteDAO {
    void addCliente(Cliente cliente) throws SQLException;
    void updateCliente(Cliente cliente) throws SQLException;
    void deleteCliente(int idCliente) throws SQLException;
    Optional<Cliente> getClienteById(int idCliente) throws SQLException;
    Optional<Cliente> getClienteByTelefono(String telefono) throws SQLException;
    List<Cliente> getAllClientes() throws SQLException;
}
