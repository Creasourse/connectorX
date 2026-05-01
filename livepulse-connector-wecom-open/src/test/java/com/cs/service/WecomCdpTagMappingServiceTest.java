package com.cs.service;

import com.cs.BaseTest;
import com.cs.exception.CommonException;
import com.cs.param.WecomCdpTagMappingParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: WecomCdpTagMappingServiceTest
 * @Author: wwd
 * @TODO: CDP标签映射 Service 测试
 * @Date: 2026/3/27
 */
@DisplayName("CDP标签映射 Service 测试")
public class WecomCdpTagMappingServiceTest extends BaseTest {

    @Autowired
    private WecomCdpTagMappingService wecomCdpTagMappingService;

    private WecomCdpTagMappingParam testParam;

    @BeforeEach
    public void setUp() {
        testParam = new WecomCdpTagMappingParam();
        testParam.setWecomCorpId(1L);
        testParam.setCdpTagName("测试CDP标签");
        testParam.setCdpTagValue("测试值");
        testParam.setWecomTagGroupId("group_001");
        testParam.setWecomTagGroupName("测试标签组");
        testParam.setWecomTagId("tag_001");
        testParam.setWecomTagName("测试企业微信标签");
    }

    @Test
    @DisplayName("测试保存CDP标签映射 - 成功")
    public void testSave_Success() throws CommonException {
        boolean result = wecomCdpTagMappingService.saveOrUpdate(testParam);
        Assertions.assertTrue(result, "保存应该成功");
    }

    @Test
    @DisplayName("测试保存CDP标签映射 - 企业ID为空抛异常")
    public void testSave_NullCorpId() {
        testParam.setWecomCorpId(null);
        Assertions.assertThrows(CommonException.class, () -> {
            wecomCdpTagMappingService.saveOrUpdate(testParam);
        });
    }

    @Test
    @DisplayName("测试保存CDP标签映射 - CDP标签名为空抛异常")
    public void testSave_NullCdpTagName() {
        testParam.setCdpTagName(null);
        Assertions.assertThrows(CommonException.class, () -> {
            wecomCdpTagMappingService.saveOrUpdate(testParam);
        });
    }

    @Test
    @DisplayName("测试同步CDP标签到企业微信 - ID为空抛异常")
    public void testSyncCdpTagsToWecom_NullId() {
        Assertions.assertThrows(CommonException.class, () -> {
            wecomCdpTagMappingService.syncCdpTagsToWecom(null);
        });
    }
}