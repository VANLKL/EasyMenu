package cn.edu.cqust.easymenu.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * StringUtils 单元测试
 * 测试边界情况：null、空字符串、空白字符串
 * 需求: 11.1, 11.2, 11.3, 11.4
 */
public class StringUtilsTest {

    // ==================== isEmpty 测试 ====================

    /**
     * 测试 isEmpty 对 null 返回 true
     */
    @Test
    public void testIsEmptyWithNull() {
        assertTrue("null 应返回 true", StringUtils.isEmpty(null));
    }

    /**
     * 测试 isEmpty 对空字符串返回 true
     */
    @Test
    public void testIsEmptyWithEmptyString() {
        assertTrue("空字符串应返回 true", StringUtils.isEmpty(""));
    }

    /**
     * 测试 isEmpty 对仅包含空格的字符串返回 true
     */
    @Test
    public void testIsEmptyWithSpaces() {
        assertTrue("仅空格应返回 true", StringUtils.isEmpty("   "));
    }

    /**
     * 测试 isEmpty 对仅包含制表符的字符串返回 true
     */
    @Test
    public void testIsEmptyWithTabs() {
        assertTrue("仅制表符应返回 true", StringUtils.isEmpty("\t\t"));
    }

    /**
     * 测试 isEmpty 对仅包含换行符的字符串返回 true
     */
    @Test
    public void testIsEmptyWithNewlines() {
        assertTrue("仅换行符应返回 true", StringUtils.isEmpty("\n\n"));
    }

    /**
     * 测试 isEmpty 对混合空白字符返回 true
     */
    @Test
    public void testIsEmptyWithMixedWhitespace() {
        assertTrue("混合空白字符应返回 true", StringUtils.isEmpty(" \t\n\r "));
    }

    /**
     * 测试 isEmpty 对非空字符串返回 false
     */
    @Test
    public void testIsEmptyWithNonEmptyString() {
        assertFalse("非空字符串应返回 false", StringUtils.isEmpty("hello"));
    }

    /**
     * 测试 isEmpty 对带空格的非空字符串返回 false
     */
    @Test
    public void testIsEmptyWithStringContainingSpaces() {
        assertFalse("带空格的字符串应返回 false", StringUtils.isEmpty("  hello  "));
    }

    // ==================== safeTrim 测试 ====================

    /**
     * 测试 safeTrim 对 null 返回空字符串
     */
    @Test
    public void testSafeTrimWithNull() {
        assertEquals("null 应返回空字符串", "", StringUtils.safeTrim(null));
    }

    /**
     * 测试 safeTrim 对空字符串返回空字符串
     */
    @Test
    public void testSafeTrimWithEmptyString() {
        assertEquals("空字符串应返回空字符串", "", StringUtils.safeTrim(""));
    }

    /**
     * 测试 safeTrim 去除首尾空格
     */
    @Test
    public void testSafeTrimRemovesLeadingAndTrailingSpaces() {
        assertEquals("应去除首尾空格", "hello", StringUtils.safeTrim("  hello  "));
    }

    /**
     * 测试 safeTrim 保留中间空格
     */
    @Test
    public void testSafeTrimPreservesMiddleSpaces() {
        assertEquals("应保留中间空格", "hello world", StringUtils.safeTrim("  hello world  "));
    }

    /**
     * 测试 safeTrim 对无空格字符串不变
     */
    @Test
    public void testSafeTrimWithNoSpaces() {
        assertEquals("无空格字符串应不变", "hello", StringUtils.safeTrim("hello"));
    }

    /**
     * 测试 safeTrim 去除制表符和换行符
     */
    @Test
    public void testSafeTrimRemovesTabsAndNewlines() {
        assertEquals("应去除制表符和换行符", "hello", StringUtils.safeTrim("\t\nhello\r\n"));
    }

    // ==================== defaultIfEmpty 测试 ====================

    /**
     * 测试 defaultIfEmpty 对 null 返回默认值
     */
    @Test
    public void testDefaultIfEmptyWithNull() {
        assertEquals("null 应返回默认值", "default", StringUtils.defaultIfEmpty(null, "default"));
    }

    /**
     * 测试 defaultIfEmpty 对空字符串返回默认值
     */
    @Test
    public void testDefaultIfEmptyWithEmptyString() {
        assertEquals("空字符串应返回默认值", "default", StringUtils.defaultIfEmpty("", "default"));
    }

    /**
     * 测试 defaultIfEmpty 对空白字符串返回默认值
     */
    @Test
    public void testDefaultIfEmptyWithWhitespace() {
        assertEquals("空白字符串应返回默认值", "default", StringUtils.defaultIfEmpty("   ", "default"));
    }

    /**
     * 测试 defaultIfEmpty 对非空字符串返回原值
     */
    @Test
    public void testDefaultIfEmptyWithNonEmptyString() {
        assertEquals("非空字符串应返回原值", "hello", StringUtils.defaultIfEmpty("hello", "default"));
    }
}
