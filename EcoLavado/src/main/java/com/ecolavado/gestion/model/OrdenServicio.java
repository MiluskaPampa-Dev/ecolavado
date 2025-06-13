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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicio {
    private String idOrden;
    private int idCliente;
    private LocalDateTime fechaRecepcion;
    private BigDecimal costoTotal;
    private String estado; // ENUM: PENDIENTE, EN_PROCESO, LISTO_PARA_RECOGER, ENTREGADO, CANCELADO
    private String observaciones;
    private LocalDateTime fechaUltimaActualizacion;

    // Relaciones para facilitar la manipulación en memoria (no se mapean directamente a DB)
    private Cliente cliente; // Podría ser cargado por el DAO si es necesario
    private List<DetalleOrden> detalles;
    private List<FotografiaOrden> fotografias;

    public OrdenServicio() {
        this.detalles = new ArrayList<>();
        this.fotografias = new ArrayList<>();
    }

    // Constructor completo
    public OrdenServicio(String idOrden, int idCliente, LocalDateTime fechaRecepcion, BigDecimal costoTotal, String estado, String observaciones, LocalDateTime fechaUltimaActualizacion) {
        this.idOrden = idOrden;
        this.idCliente = idCliente;
        this.fechaRecepcion = fechaRecepcion;
        this.costoTotal = costoTotal;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
        this.detalles = new ArrayList<>();
        this.fotografias = new ArrayList<>();
    }

    // Getters y Setters
    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) { this.fechaUltimaActualizacion = fechaUltimaActualizacion; }

    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
    public void addDetalle(DetalleOrden detalle) { this.detalles.add(detalle); }

    public List<FotografiaOrden> getFotografias() { return fotografias; }
    public void setFotografias(List<FotografiaOrden> fotografias) { this.fotografias = fotografias; }
    public void addFotografia(FotografiaOrden foto) { this.fotografias.add(foto); }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    // Método para calcular el costo total (lógica de negocio básica)
    public void calcularCostoTotal() {
        this.costoTotal = BigDecimal.ZERO;
        for (DetalleOrden detalle : detalles) {
            this.costoTotal = this.costoTotal.add(detalle.getSubtotal());
        }
    }

    @Override
    public String toString() {
        return "OrdenServicio{" +
               "idOrden='" + idOrden + '\'' +
               ", idCliente=" + idCliente +
               ", estado='" + estado + '\'' +
               ", costoTotal=" + costoTotal +
               '}';
    }
}