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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrdenServicioDAO {
    void addOrden(OrdenServicio orden) throws SQLException;
    void updateOrden(OrdenServicio orden) throws SQLException;
    void deleteOrden(String idOrden) throws SQLException;
    Optional<OrdenServicio> getOrdenById(String idOrden) throws SQLException;
    List<OrdenServicio> getOrdenesByClienteId(int idCliente) throws SQLException;
    List<OrdenServicio> getOrdenesByEstado(String estado) throws SQLException;
    List<OrdenServicio> getAllOrdenes() throws SQLException;
}