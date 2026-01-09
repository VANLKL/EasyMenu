package cn.edu.cqust.easymenu.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * PasswordUtils 单元测试
 * 测试基本功能：createStoredPassword 格式、verifyPassword 基本验证
 * 需求: 10.1, 10.2
 */
public class PasswordUtilsTest {

    /**
     * 测试 createStoredPassword 生成的格式包含冒号分隔符
     */
    @Test
    public void testCreateStoredPasswordFormat() {
        String password = "testPassword123";
        String stored = TestPasswordUtils.createStoredPassword(password);
        
        assertNotNull("存储密码不应为null", stored);
        assertTrue("存储密码应包含冒号分隔符", stored.contains(":"));
        
        String[] parts = stored.split(":");
        assertEquals("存储密码应由两部分组成（盐值:哈希）", 2, parts.length);
        assertTrue("盐值部分不应为空", parts[0].length() > 0);
        assertTrue("哈希部分不应为空", parts[1].length() > 0);
    }

    /**
     * 测试相同密码验证返回 true
     */
    @Test
    public void testVerifyPasswordWithCorrectPassword() {
        String password = "mySecurePassword";
        String stored = TestPasswordUtils.createStoredPassword(password);
        
        assertTrue("相同密码验证应返回true", 
            TestPasswordUtils.verifyPassword(password, stored));
    }

    /**
     * 测试不同密码验证返回 false
     */
    @Test
    public void testVerifyPasswordWithWrongPassword() {
        String password = "correctPassword";
        String wrongPassword = "wrongPassword";
        String stored = TestPasswordUtils.createStoredPassword(password);
        
        assertFalse("不同密码验证应返回false", 
            TestPasswordUtils.verifyPassword(wrongPassword, stored));
    }

    /**
     * 测试 null 存储密码验证返回 false
     */
    @Test
    public void testVerifyPasswordWithNullStored() {
        assertFalse("null存储密码验证应返回false", 
            TestPasswordUtils.verifyPassword("anyPassword", null));
    }

    /**
     * 测试无效格式存储密码验证返回 false
     */
    @Test
    public void testVerifyPasswordWithInvalidFormat() {
        assertFalse("无冒号格式验证应返回false", 
            TestPasswordUtils.verifyPassword("password", "invalidformat"));
        assertFalse("多冒号格式验证应返回false", 
            TestPasswordUtils.verifyPassword("password", "a:b:c"));
    }

    /**
     * 测试每次生成的存储密码不同（因为盐值随机）
     */
    @Test
    public void testCreateStoredPasswordGeneratesDifferentSalts() {
        String password = "samePassword";
        String stored1 = TestPasswordUtils.createStoredPassword(password);
        String stored2 = TestPasswordUtils.createStoredPassword(password);
        
        assertNotEquals("相同密码应生成不同的存储密码（随机盐值）", stored1, stored2);
    }

    /**
     * 测试空密码处理
     */
    @Test
    public void testEmptyPassword() {
        String stored = TestPasswordUtils.createStoredPassword("");
        assertTrue("空密码也应能正确验证", 
            TestPasswordUtils.verifyPassword("", stored));
        assertFalse("空密码与非空密码应不匹配", 
            TestPasswordUtils.verifyPassword("notEmpty", stored));
    }

    /**
     * 测试特殊字符密码
     */
    @Test
    public void testSpecialCharacterPassword() {
        String password = "P@ssw0rd!#$%^&*()中文密码";
        String stored = TestPasswordUtils.createStoredPassword(password);
        
        assertTrue("特殊字符密码应能正确验证", 
            TestPasswordUtils.verifyPassword(password, stored));
    }
}
