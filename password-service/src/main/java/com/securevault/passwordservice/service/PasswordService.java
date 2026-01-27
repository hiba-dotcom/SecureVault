package com.securevault.passwordservice.service;

import com.securevault.passwordservice.dto.*;
import com.securevault.passwordservice.model.PasswordEntry;
import com.securevault.passwordservice.repository.PasswordRepository;
import org.jboss.logging.BasicLogger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final EncryptionService encryptionService;
    private final SecurityServiceClient securityServiceClient;
    private final JwtTokenUtil jwtTokenUtil;

    public PasswordService(PasswordRepository passwordRepository,
                           EncryptionService encryptionService,
                           SecurityServiceClient securityServiceClient,
                           JwtTokenUtil jwtTokenUtil) {
        this.passwordRepository = passwordRepository;
        this.encryptionService = encryptionService;
        this.securityServiceClient = securityServiceClient;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public List<PasswordResponse> getAllPasswords(String token) {
        String username = extractUsernameFromToken(token);

        return passwordRepository.findByUserId(username).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PasswordResponse getPasswordById(Long id, String token) {
        String username = extractUsernameFromToken(token);

        return passwordRepository.findByIdAndUserId(id, username)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Password not found or access denied"));
    }

    public PasswordResponse createPassword(PasswordCreateRequest request, String token) {
        String username = extractUsernameFromToken(token);

        // Vérifier si le couple site/username existe déjà
        if (passwordRepository.existsByUserIdAndWebsiteAndUsername(username, request.getWebsite(), request.getUsername())) {
            throw new RuntimeException("A password already exists for this website and username");
        }

        // Analyser la force du mot de passe
        SecurityAnalysisResponse analysis = securityServiceClient.analyzePassword(request.getPassword());

        // Crypter le mot de passe
        String encryptedPassword;
        try {
            encryptedPassword = encryptionService.encrypt(request.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }

        // Créer l'entrée
        PasswordEntry entry = new PasswordEntry();
        entry.setUserId(username);
        entry.setWebsite(request.getWebsite());
        entry.setUsername(request.getUsername());
        entry.setEncryptedPassword(encryptedPassword);
        entry.setNotes(request.getNotes());
        entry.setStrengthScore(analysis.getStrengthScore());
        entry.setStrengthLevel(analysis.getStrengthLevel());

        passwordRepository.save(entry);

        return convertToResponse(entry);
    }

    public PasswordResponse updatePassword(Long id, PasswordUpdateRequest request, String token) {
        String username = extractUsernameFromToken(token);

        PasswordEntry entry = passwordRepository.findByIdAndUserId(id, username)
                .orElseThrow(() -> new RuntimeException("Password not found or access denied"));

        // Mettre à jour les champs
        if (request.getWebsite() != null) entry.setWebsite(request.getWebsite());
        if (request.getUsername() != null) entry.setUsername(request.getUsername());
        if (request.getNotes() != null) entry.setNotes(request.getNotes());

        // Si le mot de passe est mis à jour
        if (request.getPassword() != null) {
            // Analyser la force du nouveau mot de passe
            SecurityAnalysisResponse analysis = securityServiceClient.analyzePassword(request.getPassword());

            // Crypter le nouveau mot de passe
            try {
                String encryptedPassword = encryptionService.encrypt(request.getPassword());
                entry.setEncryptedPassword(encryptedPassword);
                entry.setStrengthScore(analysis.getStrengthScore());
                entry.setStrengthLevel(analysis.getStrengthLevel());
            } catch (Exception e) {
                throw new RuntimeException("Error encrypting password", e);
            }
        }

        passwordRepository.save(entry);

        return convertToResponse(entry);
    }

    public void deletePassword(Long id, String token) {
        String username = extractUsernameFromToken(token);

        if (!passwordRepository.existsByIdAndUserId(id, username)) {
            throw new RuntimeException("Password not found or access denied");
        }

        passwordRepository.deleteByIdAndUserId(id, username);
    }

    public List<PasswordResponse> searchPasswords(String query, String token) {
        String username = extractUsernameFromToken(token);

        return passwordRepository.findByUserIdAndWebsiteContainingIgnoreCase(username, query).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PasswordResponse analyzePassword(String password) {
        SecurityAnalysisResponse analysis = securityServiceClient.analyzePassword(password);

        PasswordResponse response = new PasswordResponse();
        response.setStrengthScore(analysis.getStrengthScore());
        response.setStrengthLevel(analysis.getStrengthLevel());

        return response;
    }

    public String generatePassword() {
        return securityServiceClient.generateStrongPassword();
    }

    private PasswordResponse convertToResponse(PasswordEntry entry) {
        PasswordResponse response = new PasswordResponse();
        response.setId(entry.getId());
        response.setWebsite(entry.getWebsite());
        response.setUsername(entry.getUsername());
        response.setNotes(entry.getNotes());
        response.setStrengthScore(entry.getStrengthScore());
        response.setStrengthLevel(entry.getStrengthLevel());

        // Note: On ne déchiffre pas le mot de passe ici pour des raisons de sécurité
        // Le déchiffrement peut être fait dans un endpoint séparé si nécessaire

        return response;
    }

    private String extractUsernameFromToken(String token) {
        try {
            // Supprimer "Bearer " si présent
            String cleanToken = token.replace("Bearer ", "").trim();
            return jwtTokenUtil.extractUsername(cleanToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}