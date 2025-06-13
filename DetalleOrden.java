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

public class DetalleOrden {
    private int idDetalle;
    private String idOrden; // FK
    private int idServicio; // FK
    private int cantidad;
    private BigDecimal subtotal;

    // Relación para facilitar la manipulación en memoria
    private Servicio servicio; // Podría ser cargado por el DAO si es necesario

    public DetalleOrden() {
    }

    public DetalleOrden(int idDetalle, String idOrden, int idServicio, int cantidad, BigDecimal subtotal) {
        this.idDetalle = idDetalle;
        this.idOrden = idOrden;
        this.idServicio = idServicio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    // Método para calcular el subtotal (lógica de negocio básica)
    public void calcularSubtotal(BigDecimal precioBase) {
        this.subtotal = precioBase.multiply(new BigDecimal(cantidad));
    }

    @Override
    public String toString() {
        return "DetalleOrden{" +
               "idDetalle=" + idDetalle +
               ", idOrden='" + idOrden + '\'' +
               ", idServicio=" + idServicio +
               ", cantidad=" + cantidad +
               ", subtotal=" + subtotal +
               '}';
    }
}
