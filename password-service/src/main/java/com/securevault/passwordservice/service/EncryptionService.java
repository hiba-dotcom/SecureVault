package com.securevault.passwordservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${encryption.key-secret}")
    private String keySecret;

    @Value("${encryption.algorithm}")
    private String algorithm;

    @Value("${encryption.key-size}")
    private int keySize;

    @Value("${encryption.iv-size}")
    private int ivSize;

    private static final int GCM_TAG_LENGTH = 128;

    public String encrypt(String plainText) throws Exception {
        // Générer une clé à partir du secret
        SecretKey secretKey = generateKeyFromSecret();

        // Générer un IV aléatoire
        byte[] iv = new byte[ivSize / 8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Configurer le cipher
        Cipher cipher = Cipher.getInstance(algorithm);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        // Chiffrer
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combiner IV + texte chiffré et encoder en Base64
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public String decrypt(String encryptedText) throws Exception {
        // Décoder de Base64
        byte[] combined = Base64.getDecoder().decode(encryptedText);

        // Extraire IV (premiers ivSize bits)
        byte[] iv = new byte[ivSize / 8];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extraire le texte chiffré (le reste)
        byte[] cipherText = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);

        // Générer la clé
        SecretKey secretKey = generateKeyFromSecret();

        // Configurer le cipher pour le déchiffrement
        Cipher cipher = Cipher.getInstance(algorithm);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        // Déchiffrer
        byte[] plainText = cipher.doFinal(cipherText);

        return new String(plainText, StandardCharsets.UTF_8);
    }

    private SecretKey generateKeyFromSecret() throws Exception {
        // Utiliser SHA-256 pour obtenir une clé de la bonne taille
        byte[] keyBytes = java.security.MessageDigest.getInstance("SHA-256")
                .digest(keySecret.getBytes(StandardCharsets.UTF_8));

        // Tronquer ou étendre à la taille souhaitée
        byte[] sizedKey = new byte[keySize / 8];
        System.arraycopy(keyBytes, 0, sizedKey, 0, Math.min(keyBytes.length, sizedKey.length));

        return new SecretKeySpec(sizedKey, "AES");
    }
}