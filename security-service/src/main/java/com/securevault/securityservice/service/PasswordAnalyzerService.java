package com.securevault.securityservice.service;

import com.securevault.securityservice.dto.PasswordAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordAnalyzerService {

    @Value("${password.requirements.min-length}")
    private int minLength;

    @Value("${password.requirements.require-uppercase}")
    private boolean requireUppercase;

    @Value("${password.requirements.require-lowercase}")
    private boolean requireLowercase;

    @Value("${password.requirements.require-digits}")
    private boolean requireDigits;

    @Value("${password.requirements.require-special}")
    private boolean requireSpecial;

    @Cacheable(value = "passwordStrength", key = "#password")
    public PasswordAnalysisResponse analyzePassword(String password) {
        long startTime = System.currentTimeMillis();

        int score = calculateStrengthScore(password);
        String level = getStrengthLevel(score);
        List<String> suggestions = generateSuggestions(password, score);

        long calculationTime = System.currentTimeMillis() - startTime;

        return new PasswordAnalysisResponse(
                score,
                level,
                false,  // compromised - sera rempli par BreachCheckService
                0,      // breachCount
                suggestions.toArray(new String[0]),
                calculationTime
        );
    }

    private int calculateStrengthScore(String password) {
        int score = 0;

        // Longueur (max 30 points)
        if (password.length() >= 16) score += 30;
        else if (password.length() >= 12) score += 25;
        else if (password.length() >= 8) score += 15;
        else if (password.length() >= 6) score += 5;

        // Complexité (max 40 points)
        if (containsUppercase(password)) score += 10;
        if (containsLowercase(password)) score += 10;
        if (containsDigits(password)) score += 10;
        if (containsSpecial(password)) score += 10;

        // Variations (max 30 points)
        if (hasNoRepeatingChars(password)) score += 10;
        if (!containsCommonPatterns(password)) score += 10;
        if (!containsCommonWords(password)) score += 10;

        return Math.min(score, 100);
    }

    private String getStrengthLevel(int score) {
        if (score >= 80) return "VERY_STRONG";
        if (score >= 60) return "STRONG";
        if (score >= 40) return "MEDIUM";
        if (score >= 20) return "WEAK";
        return "VERY_WEAK";
    }

    private List<String> generateSuggestions(String password, int score) {
        List<String> suggestions = new ArrayList<>();

        if (password.length() < minLength) {
            suggestions.add("Le mot de passe devrait avoir au moins " + minLength + " caractères");
        }

        if (requireUppercase && !containsUppercase(password)) {
            suggestions.add("Ajoutez des lettres majuscules (A-Z)");
        }

        if (requireLowercase && !containsLowercase(password)) {
            suggestions.add("Ajoutez des lettres minuscules (a-z)");
        }

        if (requireDigits && !containsDigits(password)) {
            suggestions.add("Ajoutez des chiffres (0-9)");
        }

        if (requireSpecial && !containsSpecial(password)) {
            suggestions.add("Ajoutez des caractères spéciaux (!@#$%^&*)");
        }

        if (containsCommonWords(password)) {
            suggestions.add("Évitez les mots courants (password, admin, 123456)");
        }

        if (containsSequentialChars(password)) {
            suggestions.add("Évitez les séquences (abc, 123, qwerty)");
        }

        if (password.toLowerCase().contains("password")) {
            suggestions.add("N'utilisez jamais le mot 'password'");
        }

        if (score < 60) {
            suggestions.add("Utilisez une phrase de passe plutôt qu'un mot de passe simple");
        }

        return suggestions;
    }

    // Méthodes utilitaires
    private boolean containsUppercase(String password) {
        return Pattern.compile("[A-Z]").matcher(password).find();
    }

    private boolean containsLowercase(String password) {
        return Pattern.compile("[a-z]").matcher(password).find();
    }

    private boolean containsDigits(String password) {
        return Pattern.compile("\\d").matcher(password).find();
    }

    private boolean containsSpecial(String password) {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
    }

    private boolean hasNoRepeatingChars(String password) {
        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == password.charAt(i - 1)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsCommonPatterns(String password) {
        String[] patterns = {"123", "abc", "qwerty", "azerty", "000", "111"};
        String lowerPass = password.toLowerCase();
        for (String pattern : patterns) {
            if (lowerPass.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCommonWords(String password) {
        String[] commonWords = {"password", "admin", "welcome", "login", "qwerty", "azerty", "123456"};
        String lowerPass = password.toLowerCase();
        for (String word : commonWords) {
            if (lowerPass.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSequentialChars(String password) {
        for (int i = 2; i < password.length(); i++) {
            char c1 = password.charAt(i - 2);
            char c2 = password.charAt(i - 1);
            char c3 = password.charAt(i);

            // Vérifier les séquences ascendantes et descendantes
            if ((c1 + 1 == c2 && c2 + 1 == c3) || (c1 - 1 == c2 && c2 - 1 == c3)) {
                return true;
            }
        }
        return false;
    }
}