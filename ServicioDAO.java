/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.model.Servicio;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ServicioDAO {
    void addServicio(Servicio servicio) throws SQLException;
    void updateServicio(Servicio servicio) throws SQLException;
    void deleteServicio(int idServicio) throws SQLException;
    Optional<Servicio> getServicioById(int idServicio) throws SQLException;
    Optional<Servicio> getServicioByTipo(String tipo) throws SQLException;
    List<Servicio> getAllServicios() throws SQLException;
}