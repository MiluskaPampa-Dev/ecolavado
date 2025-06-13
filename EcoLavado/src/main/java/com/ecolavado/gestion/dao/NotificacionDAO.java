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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface NotificacionDAO {
    void addNotificacion(Notificacion notificacion) throws SQLException;
    void updateNotificacion(Notificacion notificacion) throws SQLException;
    void deleteNotificacion(int idNotificacion) throws SQLException;
    Optional<Notificacion> getNotificacionById(int idNotificacion) throws SQLException;
    List<Notificacion> getNotificacionesByClienteId(int idCliente) throws SQLException;
    List<Notificacion> getNotificacionesByOrdenId(String idOrden) throws SQLException;
    List<Notificacion> getNotificacionesByEstado(String estado) throws SQLException;
    List<Notificacion> getAllNotificaciones() throws SQLException;
}