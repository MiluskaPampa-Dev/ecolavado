/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.dao;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Usando Logback para logging

public class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/ecolavado_db";
    private static final String DB_USER = "root"; // ¡Cambiar en producción!
    private static final String DB_PASSWORD = "1234"; // ¡Cambiar en producción!

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // Asegurarse de que el driver de MariaDB esté en el classpath
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // logger.info("Conexión a la base de datos establecida correctamente.");
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos: {}", e.getMessage(), e);
            throw e;
        } catch (ClassNotFoundException e) {
            logger.error("Driver de MariaDB no encontrado. Asegúrate de que mariadb-java-client.jar esté en el classpath.", e);
            throw new SQLException("Driver de base de datos no encontrado.", e);
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                // logger.info("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión a la base de datos: {}", e.getMessage(), e);
            }
        }
    }
}
