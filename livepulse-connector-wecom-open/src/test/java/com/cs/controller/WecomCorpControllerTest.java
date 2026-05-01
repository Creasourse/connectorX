package com.cs.controller;

import com.cs.BaseTest;
import com.cs.param.WecomCorpPageParam;
import com.cs.param.WecomCorpParam;
import com.cs.resp.RespResult;
import com.cs.vo.WecomCorpVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @ClassName: WecomCorpControllerTest
 * @Author: wwd
 * @TODO: 企业微信账户管理 Controller 测试
 * @Date: 2026/3/27
 */
@AutoConfigureMockMvc
@DisplayName("企业微信账户管理 Controller 测试")
public class WecomCorpControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // 测试前置处理
    }

    @Test
    @DisplayName("测试分页查询企业微信账户列表 - 成功")
    public void testPageList_Success() throws Exception {
        WecomCorpPageParam pageParam = new WecomCorpPageParam();
        pageParam.setCurrentPage(1);
        pageParam.setPageSize(10);

        String jsonContent = """
                {
                    "currentPage": 1,
                    "pageSize": 10
                }
                """;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomCorp/pageList")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
        System.out.println("分页查询结果: " + response);
    }

    @Test
    @DisplayName("测试根据ID查询企业微信账户详情 - 成功")
    public void testDetail_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/wecomCorp/detail/{wecomeCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
        System.out.println("查询详情结果: " + response);
    }

    @Test
    @DisplayName("测试保存企业微信账户 - 成功")
    public void testSave_Success() throws Exception {
        String jsonContent = """
                {
                    "companyName": "测试企业",
                    "corpId": "ww123456789",
                    "corpSecret": "test_secret"
                }
                """;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomCorp/saveOrUpdate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
        System.out.println("保存结果: " + response);
    }

    @Test
    @DisplayName("测试更新企业微信账户 - 成功")
    public void testUpdate_Success() throws Exception {
        String jsonContent = """
                {
                    "wecomeCorpId": 1,
                    "companyName": "测试企业（更新）",
                    "corpId": "ww123456789",
                    "corpSecret": "test_secret_updated"
                }
                """;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomCorp/saveOrUpdate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
        System.out.println("更新结果: " + response);
    }

    @Test
    @DisplayName("测试删除企业微信账户 - 成功")
    public void testDelete_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.delete("/wecomCorp/delete/{wecomeCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
        System.out.println("删除结果: " + response);
    }
}