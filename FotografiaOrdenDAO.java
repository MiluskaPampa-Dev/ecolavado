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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FotografiaOrdenDAO {
    void addFotografiaOrden(FotografiaOrden fotografiaOrden) throws SQLException;
    void updateFotografiaOrden(FotografiaOrden fotografiaOrden) throws SQLException;
    void deleteFotografiaOrden(int idFoto) throws SQLException;
    Optional<FotografiaOrden> getFotografiaOrdenById(int idFoto) throws SQLException;
    List<FotografiaOrden> getFotografiasByOrdenId(String idOrden) throws SQLException;
    List<FotografiaOrden> getAllFotografiasOrden() throws SQLException;
}