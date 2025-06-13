CREATE DATABASE IF NOT EXISTS ecolavado_db;
USE ecolavado_db;
-- Tabla para almacenar información de los clientes
CREATE TABLE IF NOT EXISTS Clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    email VARCHAR(100),
    preferencias TEXT,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- Tabla para definir los tipos de servicios de lavandería
CREATE TABLE IF NOT EXISTS Servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    precio_base DECIMAL(10, 2) NOT NULL CHECK (precio_base >= 0),
    tiempo_estimado_min INT CHECK (tiempo_estimado_min >= 0)
);
-- Tabla para las órdenes de servicio (tickets digitales)
CREATE TABLE IF NOT EXISTS Ordenes_Servicio (
    id_orden VARCHAR(50) PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha_recepcion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    costo_total DECIMAL(10, 2) NOT NULL CHECK (costo_total >= 0),
    estado ENUM('PENDIENTE', 'EN_PROCESO', 'LISTO_PARA_RECOGER', 'ENTREGADO', 'CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    observaciones TEXT,
    fecha_ultima_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE CASCADE
);
-- Tabla de detalle para los servicios dentro de cada orden
CREATE TABLE IF NOT EXISTS Detalle_Orden (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_orden VARCHAR(50) NOT NULL,
    id_servicio INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    subtotal DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0),
    FOREIGN KEY (id_orden) REFERENCES Ordenes_Servicio(id_orden) ON DELETE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES Servicios(id_servicio)
);
-- Tabla para almacenar las fotografías adjuntas a una orden
CREATE TABLE IF NOT EXISTS Fotografias_Orden (
    id_foto INT AUTO_INCREMENT PRIMARY KEY,
    id_orden VARCHAR(50) NOT NULL,
    url_foto VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    fecha_carga DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_orden) REFERENCES Ordenes_Servicio(id_orden) ON DELETE CASCADE
);

-- Tabla para gestionar las notificaciones enviadas a los clientes
CREATE TABLE IF NOT EXISTS Notificaciones (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_orden VARCHAR(50),
    tipo ENUM('CONFIRMACION', 'ESTADO', 'RECORDATORIO', 'ENCUESTA', 'PROMOCION') NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_envio ENUM('ENVIADO', 'FALLIDO', 'PENDIENTE') NOT NULL DEFAULT 'PENDIENTE',
    FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_orden) REFERENCES Ordenes_Servicio(id_orden) ON DELETE SET NULL
);
-- Indices
CREATE INDEX idx_clientes_telefono ON Clientes (telefono);
CREATE INDEX idx_ordenes_cliente ON Ordenes_Servicio (id_cliente);
CREATE INDEX idx_ordenes_estado ON Ordenes_Servicio (estado);
CREATE INDEX idx_detalle_orden_servicio ON Detalle_Orden (id_orden, id_servicio);
CREATE INDEX idx_notificaciones_cliente ON Notificaciones (id_cliente);
CREATE INDEX idx_notificaciones_orden ON Notificaciones (id_orden);