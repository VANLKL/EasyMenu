package cn.edu.cqust.easymenu.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class PasswordUtils {

    private static final int SALT_BYTES = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtils() {}

    public static String generateSaltBase64() {
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hashSha256Base64(String saltBase64, String plainPassword) {
        try {
            byte[] salt = Base64.decode(saltBase64, Base64.NO_WRAP);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(salt);
            digest.update(plainPassword.getBytes(StandardCharsets.UTF_8));

            byte[] hashed = digest.digest();
            return Base64.encodeToString(hashed, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    public static String createStoredPassword(String plainPassword) {
        String salt = generateSaltBase64();
        String hash = hashSha256Base64(salt, plainPassword);
        return salt + ":" + hash;
    }

    public static boolean verifyPassword(String plainPassword, String storedSaltColonHash) {
        if (storedSaltColonHash == null) return false;
        String[] parts = storedSaltColonHash.split(":");
        if (parts.length != 2) return false;

        String salt = parts[0];
        String hash = parts[1];
        String computed = hashSha256Base64(salt, plainPassword);
        return computed.equals(hash);
    }
}
