package com.securevault.passwordservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_entries")
@Data
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;  // Username de l'utilisateur

    @Column(nullable = false)
    private String website;

    @Column(nullable = false)
    private String username;

    @Column(name = "encrypted_password", nullable = false, length = 500)
    private String encryptedPassword;

    @Column(length = 1000)
    private String notes;

    @Column(name = "strength_score")
    private Integer strengthScore;

    @Column(name = "strength_level")
    private String strengthLevel;

    @Column(name = "is_compromised")
    private Boolean isCompromised = false;

    public PasswordEntry() {
    }

    public PasswordEntry(Long id, String userId, String website, String username, String encryptedPassword, String notes, Integer strengthScore, String strengthLevel, Boolean isCompromised) {
        this.id = id;
        this.userId = userId;
        this.website = website;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.notes = notes;
        this.strengthScore = strengthScore;
        this.strengthLevel = strengthLevel;
        this.isCompromised = isCompromised;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

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
}