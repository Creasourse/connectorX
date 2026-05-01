package com.cs.service;

import com.cs.BaseTest;
import com.cs.exception.CommonException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: WecomSyncServiceTest
 * @Author: wwd
 * @TODO: 企业微信数据同步 Service 测试
 * @Date: 2026/3/27
 */
@DisplayName("企业微信数据同步 Service 测试")
public class WecomSyncServiceTest extends BaseTest {

    @Autowired
    private WecomSyncService wecomSyncService;

    @Test
    @DisplayName("测试同步部门列表 - 企业ID为空抛异常")
    public void testSyncDepartments_NullCorpId() {
        Assertions.assertThrows(CommonException.class, () -> {
            wecomSyncService.syncDepartments(null);
        });
    }

    @Test
    @DisplayName("测试同步员工列表 - 企业ID为空抛异常")
    public void testSyncEmployees_NullCorpId() {
        Assertions.assertThrows(CommonException.class, () -> {
            wecomSyncService.syncEmployees(null);
        });
    }

    @Test
    @DisplayName("测试全量同步 - 企业ID为空抛异常")
    public void testSyncAll_NullCorpId() {
        Assertions.assertThrows(CommonException.class, () -> {
            wecomSyncService.syncAll(null);
        });
    }
}