package com.cs.service;

import com.cs.BaseTest;
import com.cs.entity.WecomCorp;
import com.cs.exception.CommonException;
import com.cs.param.WecomCorpPageParam;
import com.cs.param.WecomCorpParam;
import com.cs.resp.PageResult;
import com.cs.vo.WecomCorpVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName: WecomCorpServiceTest
 * @Author: wwd
 * @TODO: 企业微信账户 Service 测试
 * @Date: 2026/3/27
 */
@DisplayName("企业微信账户 Service 测试")
public class WecomCorpServiceTest extends BaseTest {

    @Autowired
    private WecomCorpService wecomCorpService;

    private WecomCorpParam testParam;
    private Long testCorpId;

    @BeforeEach
    public void setUp() {
        // 准备测试数据
        testParam = new WecomCorpParam();
        testParam.setCompanyName("测试企业");
        testParam.setCorpId("ww_test_123");
        testParam.setCorpSecret("test_secret");
    }

    @Test
    @DisplayName("测试保存企业微信账户 - 成功")
    public void testSave_Success() throws CommonException {
        boolean result = wecomCorpService.saveOrUpdate(testParam);
        Assertions.assertTrue(result, "保存应该成功");
    }

    @Test
    @DisplayName("测试保存企业微信账户 - 企业名称为空抛异常")
    public void testSave_CompanyName_Null() {
        testParam.setCompanyName(null);
        Assertions.assertThrows(CommonException.class, () -> {
            wecomCorpService.saveOrUpdate(testParam);
        });
    }

    @Test
    @DisplayName("测试分页查询企业微信账户 - 成功")
    public void testPageList_Success() throws CommonException {
        WecomCorpPageParam pageParam = new WecomCorpPageParam();
        pageParam.setCurrentPage(1);
        pageParam.setPageSize(10);

        PageResult<WecomCorpVO> result = wecomCorpService.pageList(pageParam);

        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertNotNull(result.getList(), "列表不应为空");
    }

    @Test
    @DisplayName("测试根据ID查询企业微信账户详情 - 成功")
    public void testGetDetailById_Success() throws CommonException {
        // 先保存一个测试数据
        wecomCorpService.saveOrUpdate(testParam);

        // 查询所有企业账户
        List<WecomCorp> corps = wecomCorpService.list();
        if (!corps.isEmpty()) {
            Long corpId = corps.get(0).getWecomeCorpId();
            WecomCorpVO result = wecomCorpService.getDetailById(corpId);

            Assertions.assertNotNull(result, "查询结果不应为空");
            Assertions.assertEquals(corpId, result.getWecomeCorpId(), "ID应该匹配");
        }
    }

    @Test
    @DisplayName("测试根据ID查询企业微信账户详情 - ID为空抛异常")
    public void testGetDetailById_Null() {
        Assertions.assertThrows(CommonException.class, () -> {
            wecomCorpService.getDetailById(null);
        });
    }

    @Test
    @DisplayName("测试删除企业微信账户 - 成功")
    public void testDeleteById_Success() throws CommonException {
        // 先保存一个测试数据
        wecomCorpService.saveOrUpdate(testParam);

        // 查询所有企业账户
        List<WecomCorp> corps = wecomCorpService.list();
        if (!corps.isEmpty()) {
            Long corpId = corps.get(0).getWecomeCorpId();
            boolean result = wecomCorpService.deleteById(corpId);

            Assertions.assertTrue(result, "删除应该成功");
        }
    }
}