package com.securevault.securityservice.dto;

import lombok.Data;

@Data
public class PasswordAnalysisRequest {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}