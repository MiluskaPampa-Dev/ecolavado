/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.model;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import java.time.LocalDateTime;

public class FotografiaOrden {
    private int idFoto;
    private String idOrden; // FK
    private String urlFoto;
    private String descripcion;
    private LocalDateTime fechaCarga;

    public FotografiaOrden() {
    }

    public FotografiaOrden(int idFoto, String idOrden, String urlFoto, String descripcion, LocalDateTime fechaCarga) {
        this.idFoto = idFoto;
        this.idOrden = idOrden;
        this.urlFoto = urlFoto;
        this.descripcion = descripcion;
        this.fechaCarga = fechaCarga;
    }

    // Getters y Setters
    public int getIdFoto() { return idFoto; }
    public void setIdFoto(int idFoto) { this.idFoto = idFoto; }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }

    @Override
    public String toString() {
        return "FotografiaOrden{" +
               "idFoto=" + idFoto +
               ", idOrden='" + idOrden + '\'' +
               ", urlFoto='" + urlFoto + '\'' +
               '}';
    }
}