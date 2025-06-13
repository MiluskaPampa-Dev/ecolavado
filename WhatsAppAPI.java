/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.util;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import com.ecolavado.gestion.model.Notificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Para futuras integraciones HTTP si usas librerías como Apache HttpClient o OkHttp
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.entity.StringEntity;
// import org.apache.http.util.EntityUtils;

public class WhatsAppAPI {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppAPI.class);

    // Placeholder para la URL del endpoint de la API de WhatsApp Business
    private static final String WHATSAPP_API_URL = "https://graph.facebook.com/v19.0/YOUR_PHONE_NUMBER_ID/messages";
    private static final String ACCESS_TOKEN = "YOUR_WHATSAPP_BUSINESS_API_TOKEN"; // ¡Mantener seguro!

    /**
     * Envía un mensaje a través de la API de WhatsApp Business.
     * En un escenario real, esto implicaría una llamada HTTP POST.
     * @param telefono El número de teléfono del destinatario.
     * @param mensaje El contenido del mensaje.
     * @param notificacion La instancia de Notificacion para actualizar su estado.
     * @return true si el intento de envío fue "exitoso" (la API respondió OK), false en caso de error.
     */
    public boolean enviarMensaje(String telefono, String mensaje, Notificacion notificacion) {
        logger.info("Simulando envío de WhatsApp a {}: {}", telefono, mensaje);

        // --- Simulación de respuesta de la API ---
        try {
            // Simular una latencia de red (cumple el requisito de <3 segundos)
            Thread.sleep(500 + (long)(Math.random() * 2000)); // Entre 0.5s y 2.5s

            // Simulamos un fallo aleatorio para probar la lógica de reintento
            if (Math.random() < 0.1) { // 10% de probabilidad de fallo
                logger.error("Simulación: Fallo en el envío a {} (API error).", telefono);
                notificacion.setEstadoEnvio("FALLIDO");
                return false;
            }

            logger.info("Simulación: Mensaje enviado exitosamente a {}.", telefono);
            notificacion.setEstadoEnvio("ENVIADO");
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error en la simulación de envío de WhatsApp (interrupción): {}", e.getMessage());
            notificacion.setEstadoEnvio("FALLIDO");
            return false;
        } catch (Exception e) {
            logger.error("Error en la simulación de envío de WhatsApp: {}", e.getMessage());
            notificacion.setEstadoEnvio("FALLIDO");
            return false;
        }

        /*
        // --- Implementación real (requiere librería HTTP, ej. Apache HttpClient) ---
        // Descomentar y configurar el pom.xml con las dependencias de Apache HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(WHATSAPP_API_URL);
            post.addHeader("Authorization", "Bearer " + ACCESS_TOKEN);
            post.addHeader("Content-Type", "application/json");

            // Construir el JSON del mensaje de WhatsApp Business API
            String jsonPayload = String.format(
                "{\"messaging_product\": \"whatsapp\", \"to\": \"%s\", \"type\": \"text\", \"text\": {\"body\": \"%s\"}}",
                telefono, mensaje.replace("\"", "\\\"") // Escapar comillas para JSON
            );
            post.setEntity(new StringEntity(jsonPayload, "UTF-8"));

            long startTime = System.currentTimeMillis();
            httpClient.execute(post, response -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                logger.info("Respuesta de WhatsApp API en {} ms.", duration);

                if (duration > 3000) {
                     logger.warn("La respuesta de la API de WhatsApp excedió los 3 segundos para {}.", telefono);
                }

                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode >= 200 && statusCode < 300) {
                    logger.info("Mensaje enviado con éxito a {}. Respuesta API: {}", telefono, responseBody);
                    notificacion.setEstadoEnvio("ENVIADO");
                    return true;
                } else {
                    logger.error("Fallo en el envío a {}. Código de estado: {}, Respuesta API: {}", telefono, statusCode, responseBody);
                    notificacion.setEstadoEnvio("FALLIDO");
                    return false;
                }
            });
            return true; // Asumimos que la ejecución de la llamada es exitosa, el estado se actualiza en el callback

        } catch (Exception e) {
            logger.error("Error al conectar o enviar mensaje a WhatsApp API: {}", e.getMessage(), e);
            notificacion.setEstadoEnvio("FALLIDO");
            return false;
        }
        */
    }
}