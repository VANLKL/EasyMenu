package cn.edu.cqust.easymenu.utils;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.junit.Assert.*;

/**
 * PasswordUtils 属性测试
 * Feature: easymenu-management
 */
public class PasswordUtilsPropertyTest {

    /**
     * Property 1: 密码往返一致性
     * 对于任意有效密码字符串，使用 createStoredPassword 创建存储密码后，
     * 使用 verifyPassword 验证相同密码 SHALL 返回 true。
     * 
     * **Validates: Requirements 10.3**
     */
    @Property(tries = 100)
    void passwordRoundTripConsistency(@ForAll @StringLength(min = 0, max = 100) String password) {
        // 创建存储密码
        String stored = TestPasswordUtils.createStoredPassword(password);
        
        // 验证相同密码应返回 true
        assertTrue("密码往返一致性：createStoredPassword 后 verifyPassword 应返回 true",
            TestPasswordUtils.verifyPassword(password, stored));
    }

    /**
     * Property 2: 不同密码验证返回 false
     * 对于任意两个不同的密码字符串 p1 和 p2，使用 createStoredPassword(p1) 创建存储密码后，
     * 使用 verifyPassword(p2, stored) SHALL 返回 false。
     * 
     * **Validates: Requirements 10.4**
     */
    @Property(tries = 100)
    void differentPasswordsReturnFalse(
            @ForAll @StringLength(min = 1, max = 50) String password1,
            @ForAll @StringLength(min = 1, max = 50) String password2) {
        
        // 只有当两个密码不同时才测试
        Assume.that(!password1.equals(password2));
        
        // 使用 password1 创建存储密码
        String stored = TestPasswordUtils.createStoredPassword(password1);
        
        // 使用 password2 验证应返回 false
        assertFalse("不同密码验证应返回 false",
            TestPasswordUtils.verifyPassword(password2, stored));
    }

    /**
     * Property 7: 存储密码格式
     * 对于任意有效密码，createStoredPassword 生成的字符串 SHALL 包含恰好一个冒号分隔符，
     * 且两部分都是有效的 Base64 编码。
     * 
     * **Validates: Requirements 10.1**
     */
    @Property(tries = 100)
    void storedPasswordFormat(@ForAll @StringLength(min = 0, max = 100) String password) {
        String stored = TestPasswordUtils.createStoredPassword(password);
        
        // 验证包含恰好一个冒号
        assertNotNull("存储密码不应为 null", stored);
        
        String[] parts = stored.split(":");
        assertEquals("存储密码应包含恰好一个冒号分隔符", 2, parts.length);
        
        // 验证盐值部分是有效的 Base64
        String salt = parts[0];
        assertTrue("盐值部分不应为空", salt.length() > 0);
        assertTrue("盐值应是有效的 Base64 编码", isValidBase64(salt));
        
        // 验证哈希部分是有效的 Base64
        String hash = parts[1];
        assertTrue("哈希部分不应为空", hash.length() > 0);
        assertTrue("哈希应是有效的 Base64 编码", isValidBase64(hash));
    }

    /**
     * 辅助方法：检查字符串是否是有效的 Base64 编码
     */
    private boolean isValidBase64(String str) {
        try {
            java.util.Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 额外属性：盐值随机性
     * 对于相同密码，每次调用 createStoredPassword 应生成不同的存储密码
     */
    @Property(tries = 50)
    void saltRandomness(@ForAll @StringLength(min = 1, max = 50) String password) {
        String stored1 = TestPasswordUtils.createStoredPassword(password);
        String stored2 = TestPasswordUtils.createStoredPassword(password);
        
        // 两次生成的存储密码应不同（因为盐值随机）
        assertNotEquals("相同密码应生成不同的存储密码（随机盐值）", stored1, stored2);
        
        // 但两者都应能验证原密码
        assertTrue("第一个存储密码应能验证原密码",
            TestPasswordUtils.verifyPassword(password, stored1));
        assertTrue("第二个存储密码应能验证原密码",
            TestPasswordUtils.verifyPassword(password, stored2));
    }
}
