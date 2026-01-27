package com.securevault.passwordservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAnalysisResponse {
    private Integer strengthScore;
    private String strengthLevel;
    private Boolean isCompromised;
    private Long breachCount;
    private String[] suggestions;

    public Integer getStrengthScore() {
        return strengthScore;
    }

    public void setStrengthScore(Integer strengthScore) {
        this.strengthScore = strengthScore;
    }

    public String getStrengthLevel() {
        return strengthLevel;
    }

    public void setStrengthLevel(String strengthLevel) {
        this.strengthLevel = strengthLevel;
    }

    public Boolean getCompromised() {
        return isCompromised;
    }

    public void setCompromised(Boolean compromised) {
        isCompromised = compromised;
    }

    public Long getBreachCount() {
        return breachCount;
    }

    public void setBreachCount(Long breachCount) {
        this.breachCount = breachCount;
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public SecurityAnalysisResponse(Integer strengthScore, String strengthLevel, Boolean isCompromised, Long breachCount, String[] suggestions) {
        this.strengthScore = strengthScore;
        this.strengthLevel = strengthLevel;
        this.isCompromised = isCompromised;
        this.breachCount = breachCount;
        this.suggestions = suggestions;
    }
}