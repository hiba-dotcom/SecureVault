package com.securevault.securityservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class BreachCheckService {

    @Value("${hibp.api.url}")
    private String hibpApiUrl;

    @Value("${hibp.api.user-agent}")
    private String userAgent;

    private final WebClient webClient;

    public BreachCheckService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(hibpApiUrl)
                .defaultHeader("User-Agent", userAgent)
                .build();
    }

    @Cacheable(value = "breachCheck", key = "#password")
    public boolean isPasswordCompromised(String password) {
        try {
            String sha1Hash = sha1(password);
            String prefix = sha1Hash.substring(0, 5);
            String suffix = sha1Hash.substring(5).toUpperCase();

            String response = webClient.get()
                    .uri("/range/{prefix}", prefix)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorReturn("")  // En cas d'erreur, retourner vide
                    .block();

            return response != null && response.contains(suffix);

        } catch (Exception e) {
            // En cas d'erreur (pas de connexion internet), retourner false
            System.err.println("Error checking password breach: " + e.getMessage());
            return false;
        }
    }

    @Cacheable(value = "breachCount", key = "#password")
    public long getBreachCount(String password) {
        try {
            String sha1Hash = sha1(password);
            String prefix = sha1Hash.substring(0, 5);
            String suffix = sha1Hash.substring(5).toUpperCase();

            String response = webClient.get()
                    .uri("/range/{prefix}", prefix)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorReturn("")
                    .block();

            if (response != null && response.contains(suffix)) {
                String[] lines = response.split("\n");
                for (String line : lines) {
                    if (line.startsWith(suffix)) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            return Long.parseLong(parts[1].trim());
                        }
                    }
                }
            }

            return 0;

        } catch (Exception e) {
            System.err.println("Error getting breach count: " + e.getMessage());
            return 0;
        }
    }

    private String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
}