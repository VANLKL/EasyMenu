package cn.edu.cqust.easymenu.utils;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.junit.Assert.*;

/**
 * StringUtils 属性测试
 * Feature: easymenu-management
 */
public class StringUtilsPropertyTest {

    /**
     * Property 3: StringUtils.isEmpty 正确性
     * 对于任意字符串 s，isEmpty(s) 返回 true 当且仅当 s 为 null 或 s.trim() 为空字符串。
     * 
     * **Validates: Requirements 11.1, 11.2**
     */
    @Property(tries = 100)
    void isEmptyCorrectness(@ForAll @StringLength(min = 0, max = 100) String str) {
        boolean expected = str == null || str.trim().isEmpty();
        boolean actual = StringUtils.isEmpty(str);
        
        assertEquals("isEmpty 应返回 (str == null || str.trim().isEmpty())", expected, actual);
    }

    /**
     * Property 3 补充：isEmpty 对 null 返回 true
     */
    @Example
    void isEmptyWithNull() {
        assertTrue("isEmpty(null) 应返回 true", StringUtils.isEmpty(null));
    }

    /**
     * Property 3 补充：isEmpty 对纯空白字符串返回 true
     */
    @Property(tries = 50)
    void isEmptyWithWhitespaceOnly(@ForAll("whitespaceStrings") String whitespace) {
        assertTrue("isEmpty 对纯空白字符串应返回 true", StringUtils.isEmpty(whitespace));
    }

    /**
     * Property 3 补充：isEmpty 对非空非空白字符串返回 false
     */
    @Property(tries = 100)
    void isEmptyWithNonEmptyString(@ForAll @StringLength(min = 1, max = 50) String str) {
        // 确保字符串包含非空白字符
        Assume.that(str.trim().length() > 0);
        
        assertFalse("isEmpty 对非空非空白字符串应返回 false", StringUtils.isEmpty(str));
    }

    /**
     * Property 4: StringUtils.safeTrim 正确性
     * 对于任意非 null 字符串 s，safeTrim(s) SHALL 返回 s.trim()；
     * 对于 null，SHALL 返回空字符串。
     * 
     * **Validates: Requirements 11.3, 11.4**
     */
    @Property(tries = 100)
    void safeTrimCorrectness(@ForAll @StringLength(min = 0, max = 100) String str) {
        String expected = str == null ? "" : str.trim();
        String actual = StringUtils.safeTrim(str);
        
        assertEquals("safeTrim 应返回 (str == null ? \"\" : str.trim())", expected, actual);
    }

    /**
     * Property 4 补充：safeTrim 对 null 返回空字符串
     */
    @Example
    void safeTrimWithNull() {
        assertEquals("safeTrim(null) 应返回空字符串", "", StringUtils.safeTrim(null));
    }

    /**
     * Property 4 补充：safeTrim 等价于 trim（对非 null）
     */
    @Property(tries = 100)
    void safeTrimEquivalentToTrim(@ForAll @StringLength(min = 0, max = 100) String str) {
        assertEquals("safeTrim 应等价于 trim", str.trim(), StringUtils.safeTrim(str));
    }

    /**
     * 额外属性：safeTrim 幂等性
     * safeTrim(safeTrim(s)) == safeTrim(s)
     */
    @Property(tries = 50)
    void safeTrimIdempotent(@ForAll @StringLength(min = 0, max = 50) String str) {
        String once = StringUtils.safeTrim(str);
        String twice = StringUtils.safeTrim(once);
        
        assertEquals("safeTrim 应是幂等的", once, twice);
    }

    /**
     * 额外属性：defaultIfEmpty 正确性
     */
    @Property(tries = 100)
    void defaultIfEmptyCorrectness(
            @ForAll @StringLength(min = 0, max = 50) String str,
            @ForAll @StringLength(min = 1, max = 20) String defaultValue) {
        
        String result = StringUtils.defaultIfEmpty(str, defaultValue);
        
        if (StringUtils.isEmpty(str)) {
            assertEquals("空字符串应返回默认值", defaultValue, result);
        } else {
            assertEquals("非空字符串应返回原值", str, result);
        }
    }

    // ==================== 自定义生成器 ====================

    /**
     * 生成纯空白字符串
     */
    @Provide
    Arbitrary<String> whitespaceStrings() {
        return Arbitraries.of(" ", "\t", "\n", "\r", "  ", "\t\t", "\n\n", " \t\n\r ", "    ");
    }
}
