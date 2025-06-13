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

public class Notificacion {
    private int idNotificacion;
    private int idCliente; // FK
    private String idOrden; // FK, puede ser nulo
    private String tipo; // ENUM: CONFIRMACION, ESTADO, RECORDATORIO, ENCUESTA, PROMOCION
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private String estadoEnvio; // ENUM: ENVIADO, FALLIDO, PENDIENTE

    public Notificacion() {
    }

    public Notificacion(int idNotificacion, int idCliente, String idOrden, String tipo, String mensaje, LocalDateTime fechaEnvio, String estadoEnvio) {
        this.idNotificacion = idNotificacion;
        this.idCliente = idCliente;
        this.idOrden = idOrden;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.estadoEnvio = estadoEnvio;
    }

    // Getters y Setters
    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getEstadoEnvio() { return estadoEnvio; }
    public void setEstadoEnvio(String estadoEnvio) { this.estadoEnvio = estadoEnvio; }

    @Override
    public String toString() {
        return "Notificacion{" +
               "idNotificacion=" + idNotificacion +
               ", idCliente=" + idCliente +
               ", idOrden='" + idOrden + '\'' +
               ", tipo='" + tipo + '\'' +
               ", estadoEnvio='" + estadoEnvio + '\'' +
               '}';
    }
}