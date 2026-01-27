package com.securevault.securityservice.dto;

import lombok.Data;

@Data
public class GeneratePasswordRequest {
    private int length = 16;
    private boolean includeUppercase = true;
    private boolean includeLowercase = true;
    private boolean includeDigits = true;
    private boolean includeSpecial = true;
    private boolean excludeAmbiguous = true;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isIncludeUppercase() {
        return includeUppercase;
    }

    public void setIncludeUppercase(boolean includeUppercase) {
        this.includeUppercase = includeUppercase;
    }

    public boolean isIncludeLowercase() {
        return includeLowercase;
    }

    public void setIncludeLowercase(boolean includeLowercase) {
        this.includeLowercase = includeLowercase;
    }

    public boolean isIncludeDigits() {
        return includeDigits;
    }

    public void setIncludeDigits(boolean includeDigits) {
        this.includeDigits = includeDigits;
    }

    public boolean isIncludeSpecial() {
        return includeSpecial;
    }

    public void setIncludeSpecial(boolean includeSpecial) {
        this.includeSpecial = includeSpecial;
    }

    public boolean isExcludeAmbiguous() {
        return excludeAmbiguous;
    }

    public void setExcludeAmbiguous(boolean excludeAmbiguous) {
        this.excludeAmbiguous = excludeAmbiguous;
    }
}