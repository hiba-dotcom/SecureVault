package com.securevault.securityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordAnalysisResponse {
    private int strengthScore;           // 0-100
    private String strengthLevel;        // VERY_WEAK, WEAK, MEDIUM, STRONG, VERY_STRONG
    private boolean compromised;         // Si le mot de passe a été compromis
    private long breachCount;            // Nombre de fuites
    private String[] suggestions;        // Suggestions d'amélioration
    private long calculationTimeMs;      // Temps de calcul en ms

    public PasswordAnalysisResponse(int strengthScore, String strengthLevel, boolean compromised, long breachCount, String[] suggestions, long calculationTimeMs) {
        this.strengthScore = strengthScore;
        this.strengthLevel = strengthLevel;
        this.compromised = compromised;
        this.breachCount = breachCount;
        this.suggestions = suggestions;
        this.calculationTimeMs = calculationTimeMs;
    }

    public int getStrengthScore() {
        return strengthScore;
    }

    public void setStrengthScore(int strengthScore) {
        this.strengthScore = strengthScore;
    }

    public String getStrengthLevel() {
        return strengthLevel;
    }

    public void setStrengthLevel(String strengthLevel) {
        this.strengthLevel = strengthLevel;
    }

    public boolean isCompromised() {
        return compromised;
    }

    public void setCompromised(boolean compromised) {
        this.compromised = compromised;
    }

    public long getBreachCount() {
        return breachCount;
    }

    public void setBreachCount(long breachCount) {
        this.breachCount = breachCount;
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public long getCalculationTimeMs() {
        return calculationTimeMs;
    }

    public void setCalculationTimeMs(long calculationTimeMs) {
        this.calculationTimeMs = calculationTimeMs;
    }
}