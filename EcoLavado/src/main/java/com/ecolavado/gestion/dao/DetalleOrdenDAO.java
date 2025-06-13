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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DetalleOrdenDAO {
    void addDetalleOrden(DetalleOrden detalleOrden) throws SQLException;
    void updateDetalleOrden(DetalleOrden detalleOrden) throws SQLException;
    void deleteDetalleOrden(int idDetalle) throws SQLException;
    Optional<DetalleOrden> getDetalleOrdenById(int idDetalle) throws SQLException;
    List<DetalleOrden> getDetallesByOrdenId(String idOrden) throws SQLException;
    List<DetalleOrden> getAllDetallesOrden() throws SQLException;
}
