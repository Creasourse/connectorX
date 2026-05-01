package com.cs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @ClassName: ApplicationTest
 * @Author: wwd
 * @TODO: 应用启动测试
 * @Date: 2026/3/27
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("应用启动测试")
public class ApplicationTest extends BaseTest {

    @Test
    @DisplayName("测试应用上下文加载 - 成功")
    public void testContextLoads() {
        // 如果能执行到这里，说明Spring上下文加载成功
        Assertions.assertTrue(true, "应用上下文加载成功");
    }
}