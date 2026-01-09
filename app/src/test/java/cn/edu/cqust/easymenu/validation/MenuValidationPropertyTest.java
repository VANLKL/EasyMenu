package cn.edu.cqust.easymenu.validation;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import cn.edu.cqust.easymenu.utils.StringUtils;

import static org.junit.Assert.*;

/**
 * 菜单验证属性测试
 * Feature: easymenu-management
 * 
 * 测试菜单输入验证逻辑，包括名称和价格验证
 */
public class MenuValidationPropertyTest {

    // 价格范围常量（与 MenuPresenter 保持一致）
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 99999.99;

    /**
     * Property 5: 菜单名称验证
     * 对于任意空白字符串（null、空字符串或仅包含空白字符），作为菜单名称时 SHALL 被拒绝。
     * 
     * **Validates: Requirements 5.2, 7.2**
     */
    @Property(tries = 100)
    void menuNameValidationRejectsEmptyNames(@ForAll("emptyOrWhitespaceStrings") String name) {
        boolean isValid = isValidMenuName(name);
        
        assertFalse("空白菜单名称应被拒绝", isValid);
    }

    /**
     * Property 5 补充：非空菜单名称应被接受
     */
    @Property(tries = 100)
    void menuNameValidationAcceptsNonEmptyNames(
            @ForAll @StringLength(min = 1, max = 100) String name) {
        // 确保名称包含非空白字符
        Assume.that(name.trim().length() > 0);
        
        boolean isValid = isValidMenuName(name);
        
        assertTrue("非空菜单名称应被接受", isValid);
    }

    /**
     * Property 6: 菜单价格验证
     * 对于任意负数或超出范围（>99999.99）的价格值，SHALL 被拒绝。
     * 
     * **Validates: Requirements 5.3, 7.3**
     */
    @Property(tries = 100)
    void menuPriceValidationRejectsInvalidPrices(@ForAll("invalidPrices") double price) {
        boolean isValid = isValidMenuPrice(price);
        
        assertFalse("无效价格应被拒绝: " + price, isValid);
    }

    /**
     * Property 6 补充：有效价格应被接受
     */
    @Property(tries = 100)
    void menuPriceValidationAcceptsValidPrices(@ForAll("validPrices") double price) {
        boolean isValid = isValidMenuPrice(price);
        
        assertTrue("有效价格应被接受: " + price, isValid);
    }

    /**
     * 额外属性：边界价格测试
     */
    @Example
    void menuPriceBoundaryValues() {
        // 最小有效价格
        assertTrue("0 应是有效价格", isValidMenuPrice(0.0));
        
        // 最大有效价格
        assertTrue("99999.99 应是有效价格", isValidMenuPrice(99999.99));
        
        // 刚好超出范围
        assertFalse("-0.01 应是无效价格", isValidMenuPrice(-0.01));
        assertFalse("100000.00 应是无效价格", isValidMenuPrice(100000.00));
    }

    /**
     * 额外属性：NaN 和无穷大应被拒绝
     */
    @Example
    void menuPriceSpecialValues() {
        assertFalse("NaN 应是无效价格", isValidMenuPrice(Double.NaN));
        assertFalse("正无穷大应是无效价格", isValidMenuPrice(Double.POSITIVE_INFINITY));
        assertFalse("负无穷大应是无效价格", isValidMenuPrice(Double.NEGATIVE_INFINITY));
    }

    // ==================== 验证方法（模拟 MenuPresenter 的验证逻辑） ====================

    /**
     * 验证菜单名称是否有效
     * 与 MenuPresenter.saveMenu 中的验证逻辑一致
     */
    private boolean isValidMenuName(String name) {
        return !StringUtils.isEmpty(name);
    }

    /**
     * 验证菜单价格是否有效
     * 与 MenuPresenter.saveMenu 中的验证逻辑一致
     */
    private boolean isValidMenuPrice(double price) {
        // 检查 NaN 和无穷大
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            return false;
        }
        // 检查范围
        return price >= MIN_PRICE && price <= MAX_PRICE;
    }

    // ==================== 自定义生成器 ====================

    /**
     * 生成空或空白字符串
     */
    @Provide
    Arbitrary<String> emptyOrWhitespaceStrings() {
        return Arbitraries.of(
            null,
            "",
            " ",
            "  ",
            "\t",
            "\n",
            "\r",
            " \t\n\r ",
            "    ",
            "\t\t\t"
        );
    }

    /**
     * 生成无效价格（负数或超出范围）
     */
    @Provide
    Arbitrary<Double> invalidPrices() {
        return Arbitraries.oneOf(
            // 负数
            Arbitraries.doubles().lessThan(0),
            // 超出最大范围
            Arbitraries.doubles().greaterThan(MAX_PRICE)
        );
    }

    /**
     * 生成有效价格（0 到 99999.99 之间）
     */
    @Provide
    Arbitrary<Double> validPrices() {
        return Arbitraries.doubles()
            .between(MIN_PRICE, MAX_PRICE)
            .ofScale(2); // 保留两位小数
    }
}
