package com.example.clientapp.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    private SecurityUtil() {
    }

    public static String generateRandomState() {
        return generateRandomState(16);
    }

    public static String generateRandomState(int lengthBytes) {
        byte[] randomBytes = new byte[lengthBytes];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
