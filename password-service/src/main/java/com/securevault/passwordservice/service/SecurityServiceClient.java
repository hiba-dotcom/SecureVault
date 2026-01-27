package com.securevault.passwordservice.service;

import com.securevault.passwordservice.dto.SecurityAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Service
public class SecurityServiceClient {

    private final WebClient securityWebClient;

    public SecurityServiceClient(WebClient securityWebClient) {
        this.securityWebClient = securityWebClient;
    }

    public SecurityAnalysisResponse analyzePassword(String password) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("password", password);

            Map response = securityWebClient.post()
                    .uri("/security/analyze")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorReturn(new HashMap<>())
                    .block();

            if (response != null && !response.isEmpty()) {
                return new SecurityAnalysisResponse(
                        (Integer) response.get("strengthScore"),
                        (String) response.get("strengthLevel"),
                        (Boolean) response.get("compromised"),
                        response.get("breachCount") != null ? Long.parseLong(response.get("breachCount").toString()) : 0L,
                        response.get("suggestions") != null ?
                                ((java.util.List<String>) response.get("suggestions")).toArray(new String[0]) :
                                new String[0]
                );
            }

        } catch (Exception e) {
            System.err.println("Error calling security service: " + e.getMessage());
        }

        // Retourner une réponse par défaut en cas d'erreur
        return new SecurityAnalysisResponse(0, "UNKNOWN", false, 0L, new String[]{});
    }

    public String generateStrongPassword() {
        try {
            Map response = securityWebClient.get()
                    .uri("/security/generate/quick")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorReturn(new HashMap<>())
                    .block();

            return response != null ? response.get("password").toString() : "ErrorGeneratingPassword";

        } catch (Exception e) {
            System.err.println("Error generating password: " + e.getMessage());
            return "ErrorGeneratingPassword";
        }
    }
}