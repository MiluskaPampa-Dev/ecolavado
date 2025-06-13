/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecolavado.gestion.util;

/**
 *
 * @author Maria Pampa y Jose Perez
 */
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Para hashing de contraseñas, usar Spring Security Crypto o jBCrypt
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Si usas Spring

public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    // CLAVE SECRETA: En un entorno de producción, esta clave NUNCA debe estar hardcodeada.
    // Debe ser gestionada de forma segura (variables de entorno, KeyStore, HashiCorp Vault, etc.)
    private static final String AES_KEY_STRING = "ThisIsASecretKeyForAESEncryption123"; // 32 bytes = 256 bits
    private static SecretKey secretKey;

    static {
        try {
            // Usamos un algoritmo de clave fija por simplicidad, en real usar KeyGenerator
            secretKey = new SecretKeySpec(AES_KEY_STRING.getBytes("UTF-8"), "AES");
        } catch (Exception e) {
            logger.error("Error al inicializar la clave de encriptación: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo inicializar la seguridad de encriptación.", e);
        }
    }

    /**
     * Encripta una cadena de texto usando AES.
     * @param data La cadena a encriptar.
     * @return La cadena encriptada en formato Base64.
     * @throws Exception Si ocurre un error durante la encriptación.
     */
    public static String encrypt(String data) throws Exception {
        if (data == null || data.isEmpty()) {
            return data;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Evitar ECB en producción real
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.error("Error al encriptar datos: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Desencripta una cadena de texto encriptada con AES.
     * @param encryptedData La cadena encriptada en formato Base64.
     * @return La cadena desencriptada.
     * @throws Exception Si ocurre un error durante la desencriptación.
     */
    public static String decrypt(String encryptedData) throws Exception {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Evitar ECB en producción real
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            logger.error("Error al desencriptar datos: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Ejemplo para hashing de contraseñas (no usado en este contexto pero importante para seguridad)
    /*
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
    public static boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    */
}
