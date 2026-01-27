package com.securevault.passwordservice.controller;

import com.securevault.passwordservice.dto.*;
import com.securevault.passwordservice.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/passwords")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping
    public ResponseEntity<List<PasswordResponse>> getAllPasswords(
            @RequestHeader("Authorization") String token) {
        List<PasswordResponse> passwords = passwordService.getAllPasswords(token);
        return ResponseEntity.ok(passwords);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasswordResponse> getPasswordById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        PasswordResponse password = passwordService.getPasswordById(id, token);
        return ResponseEntity.ok(password);
    }

    @PostMapping
    public ResponseEntity<PasswordResponse> createPassword(
            @Valid @RequestBody PasswordCreateRequest request,
            @RequestHeader("Authorization") String token) {
        PasswordResponse password = passwordService.createPassword(request, token);
        return ResponseEntity.ok(password);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasswordResponse> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequest request,
            @RequestHeader("Authorization") String token) {
        PasswordResponse password = passwordService.updatePassword(id, request, token);
        return ResponseEntity.ok(password);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        passwordService.deletePassword(id, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PasswordResponse>> searchPasswords(
            @RequestParam String query,
            @RequestHeader("Authorization") String token) {
        List<PasswordResponse> passwords = passwordService.searchPasswords(query, token);
        return ResponseEntity.ok(passwords);
    }

    @PostMapping("/analyze")
    public ResponseEntity<PasswordResponse> analyzePassword(
            @Valid @RequestBody PasswordCreateRequest request) {
        PasswordResponse analysis = passwordService.analyzePassword(request.getPassword());
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generatePassword() {
        String password = passwordService.generatePassword();
        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Password Service is UP");
    }
}