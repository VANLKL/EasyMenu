package cn.edu.cqust.easymenu.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 测试专用的 PasswordUtils 实现
 * 使用 Java 标准库的 Base64 替代 Android 的 Base64
 * 逻辑与生产代码 PasswordUtils 完全一致
 */
public final class TestPasswordUtils {

    private static final int SALT_BYTES = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private TestPasswordUtils() {}

    public static String generateSaltBase64() {
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashSha256Base64(String saltBase64, String plainPassword) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(salt);
            digest.update(plainPassword.getBytes(StandardCharsets.UTF_8));

            byte[] hashed = digest.digest();
            return Base64.getEncoder().encodeToString(hashed);
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
