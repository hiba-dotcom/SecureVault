package com.securevault.securityservice.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordGeneratorService {

    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String AMBIGUOUS = "O0Il1";

    private final SecureRandom random = new SecureRandom();

    public String generatePassword(int length,
                                   boolean includeUppercase,
                                   boolean includeLowercase,
                                   boolean includeDigits,
                                   boolean includeSpecial,
                                   boolean excludeAmbiguous) {

        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4");
        }

        StringBuilder charPool = new StringBuilder();

        if (includeUppercase) {
            charPool.append(excludeAmbiguous ? removeAmbiguous(UPPERCASE) : UPPERCASE);
        }
        if (includeLowercase) {
            charPool.append(excludeAmbiguous ? removeAmbiguous(LOWERCASE) : LOWERCASE);
        }
        if (includeDigits) {
            charPool.append(excludeAmbiguous ? removeAmbiguous(DIGITS) : DIGITS);
        }
        if (includeSpecial) {
            charPool.append(SPECIAL);
        }

        if (charPool.length() == 0) {
            throw new IllegalArgumentException("At least one character set must be selected");
        }

        // S'assurer que le mot de passe contient au moins un caractère de chaque type sélectionné
        List<Character> passwordChars = new ArrayList<>();

        if (includeUppercase) {
            String uppercaseSet = excludeAmbiguous ? removeAmbiguous(UPPERCASE) : UPPERCASE;
            passwordChars.add(uppercaseSet.charAt(random.nextInt(uppercaseSet.length())));
        }
        if (includeLowercase) {
            String lowercaseSet = excludeAmbiguous ? removeAmbiguous(LOWERCASE) : LOWERCASE;
            passwordChars.add(lowercaseSet.charAt(random.nextInt(lowercaseSet.length())));
        }
        if (includeDigits) {
            String digitsSet = excludeAmbiguous ? removeAmbiguous(DIGITS) : DIGITS;
            passwordChars.add(digitsSet.charAt(random.nextInt(digitsSet.length())));
        }
        if (includeSpecial) {
            passwordChars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        }

        // Remplir le reste avec des caractères aléatoires
        while (passwordChars.size() < length) {
            passwordChars.add(charPool.charAt(random.nextInt(charPool.length())));
        }

        // Mélanger
        shuffleList(passwordChars);

        // Convertir en String
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private void shuffleList(List<Character> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    private String removeAmbiguous(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (AMBIGUOUS.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }

    public String generateQuickPassword() {
        return generatePassword(16, true, true, true, true, true);
    }
}