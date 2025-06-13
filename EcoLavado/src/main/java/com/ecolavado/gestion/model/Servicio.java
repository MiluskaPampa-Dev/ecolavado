/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.model;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import java.math.BigDecimal;

public class Servicio {
    private int idServicio;
    private String tipo;
    private String descripcion;
    private BigDecimal precioBase;
    private int tiempoEstimadoMin;

    public Servicio() {
    }

    public Servicio(int idServicio, String tipo, String descripcion, BigDecimal precioBase, int tiempoEstimadoMin) {
        this.idServicio = idServicio;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.tiempoEstimadoMin = tiempoEstimadoMin;
    }

    // Getters y Setters
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public int getTiempoEstimadoMin() { return tiempoEstimadoMin; }
    public void setTiempoEstimadoMin(int tiempoEstimadoMin) { this.tiempoEstimadoMin = tiempoEstimadoMin; }

    @Override
    public String toString() {
        return "Servicio{" +
               "idServicio=" + idServicio +
               ", tipo='" + tipo + '\'' +
               ", precioBase=" + precioBase +
               '}';
    }
}
