package com.cs;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: BaseTest
 * @Author: wwd
 * @TODO: 测试基类
 * @Date: 2026/3/27
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConnWecomOpenApp.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseTest {

    // 测试基类，所有测试类继承此类
    // @Transactional 确保测试后的数据回滚，不污染数据库
}