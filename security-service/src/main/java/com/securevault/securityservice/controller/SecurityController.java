package com.securevault.securityservice.controller;

import com.securevault.securityservice.dto.*;
import com.securevault.securityservice.service.BreachCheckService;
import com.securevault.securityservice.service.PasswordAnalyzerService;
import com.securevault.securityservice.service.PasswordGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final PasswordAnalyzerService passwordAnalyzerService;
    private final BreachCheckService breachCheckService;
    private final PasswordGeneratorService passwordGeneratorService;

    public SecurityController(PasswordAnalyzerService passwordAnalyzerService,
                              BreachCheckService breachCheckService,
                              PasswordGeneratorService passwordGeneratorService) {
        this.passwordAnalyzerService = passwordAnalyzerService;
        this.breachCheckService = breachCheckService;
        this.passwordGeneratorService = passwordGeneratorService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<PasswordAnalysisResponse> analyzePassword(
            @Valid @RequestBody PasswordAnalysisRequest request) {

        PasswordAnalysisResponse analysis = passwordAnalyzerService.analyzePassword(request.getPassword());

        // Vérifier si le mot de passe a été compromis (optionnel - besoin internet)
        boolean isCompromised = breachCheckService.isPasswordCompromised(request.getPassword());
        analysis.setCompromised(isCompromised);

        if (isCompromised) {
            long breachCount = breachCheckService.getBreachCount(request.getPassword());
            analysis.setBreachCount(breachCount);
        }

        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generatePassword(
            @Valid @RequestBody GeneratePasswordRequest request) {

        String password = passwordGeneratorService.generatePassword(
                request.getLength(),
                request.isIncludeUppercase(),
                request.isIncludeLowercase(),
                request.isIncludeDigits(),
                request.isIncludeSpecial(),
                request.isExcludeAmbiguous()
        );

        // Analyser le mot de passe généré
        PasswordAnalysisResponse analysis = passwordAnalyzerService.analyzePassword(password);

        Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        response.put("strengthScore", analysis.getStrengthScore());
        response.put("strengthLevel", analysis.getStrengthLevel());
        response.put("suggestions", analysis.getSuggestions());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/generate/quick")
    public ResponseEntity<Map<String, String>> generateQuickPassword() {
        String password = passwordGeneratorService.generateQuickPassword();

        Map<String, String> response = new HashMap<>();
        response.put("password", password);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Security Service is UP");
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "security-service");
        response.put("status", "active");
        response.put("timestamp", System.currentTimeMillis());

        // Test de génération
        String testPassword = passwordGeneratorService.generateQuickPassword();
        PasswordAnalysisResponse analysis = passwordAnalyzerService.analyzePassword(testPassword);

        response.put("testPassword", testPassword);
        response.put("testScore", analysis.getStrengthScore());
        response.put("testLevel", analysis.getStrengthLevel());

        return ResponseEntity.ok(response);
    }
}