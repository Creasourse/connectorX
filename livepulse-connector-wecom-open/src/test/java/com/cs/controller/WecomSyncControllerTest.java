package com.cs.controller;

import com.cs.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @ClassName: WecomSyncControllerTest
 * @Author: wwd
 * @TODO: 企业微信数据同步 Controller 测试
 * @Date: 2026/3/27
 */
@AutoConfigureMockMvc
@DisplayName("企业微信数据同步 Controller 测试")
public class WecomSyncControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("测试同步部门列表 - 成功")
    public void testSyncDepartments_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomSync/syncDepartments/{wecomCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("同步部门结果: " + response);
    }

    @Test
    @DisplayName("测试同步员工列表 - 成功")
    public void testSyncEmployees_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomSync/syncEmployees/{wecomCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("同步员工结果: " + response);
    }

    @Test
    @DisplayName("测试全量同步（部门+员工） - 成功")
    public void testSyncAll_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomSync/syncAll/{wecomCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("全量同步结果: " + response);
    }

    @Test
    @DisplayName("测试同步外部联系人 - 成功")
    public void testSyncExternalContacts_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomSync/syncAllExternalContacts/{wecomCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("同步外部联系人结果: " + response);
    }

    @Test
    @DisplayName("测试同步客户群 - 成功")
    public void testSyncCustomerGroups_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomSync/syncAllCustomerGroups/{wecomCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("同步客户群结果: " + response);
    }

    @Test
    @DisplayName("测试同步企业标签库 - 成功")
    public void testSyncCorpTags_Success() throws Exception {
        Long corpId = 1L;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/wecomSync/syncCorpTags/{wecomCorpId}", corpId)
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("同步企业标签库结果: " + response);
    }
}