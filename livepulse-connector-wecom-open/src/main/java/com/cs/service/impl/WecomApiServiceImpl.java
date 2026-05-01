package com.cs.service.impl;

import com.cs.dto.*;
import com.cs.entity.WecomCorp;
import com.cs.exception.CommonException;
import com.cs.service.RedisCacheService;
import com.cs.service.WecomApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: WecomApiServiceImpl
 * @Author: wwd
 * @TODO: 企业微信API调用服务实现
 * @Date: 2026/3/21
 */
@Slf4j
@Service
public class WecomApiServiceImpl implements WecomApiService {

    private static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    private static final String DEPARTMENT_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=%s";
    private static final String EMPLOYEE_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%s&fetch_child=%d";
    private static final String EMPLOYEE_DETAIL_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";

    // 外部联系人API
    private static final String EXTERNAL_CONTACT_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_ext_contact_list?access_token=%s&userid=%s";
    private static final String EXTERNAL_CONTACT_DETAIL_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/batch/get_by_user?access_token=%s";

    // 客户群API
    private static final String CUSTOMER_GROUP_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/list?access_token=%s";
    private static final String GROUP_DETAIL_URL = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get?access_token=%s";

    // 标签API
    private static final String CORP_TAG_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_corp_tag_list?access_token=%s";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisCacheService<String> redisCacheService;

    private static final String CACHE_KEY_PREFIX = "wecom:corp:accessToken:";

    @Override
    public String getAccessToken(WecomCorp wecomCorp) throws CommonException {
        if (wecomCorp == null || StringUtils.isBlank(wecomCorp.getCorpId()) || StringUtils.isBlank(wecomCorp.getCorpSecret())) {
            throw new CommonException("企业微信配置信息不完整");
        }

        String cacheKey = CACHE_KEY_PREFIX + wecomCorp.getCorpId();
        String cacheValue = redisCacheService.get(cacheKey);
        if (StringUtils.isNotBlank(cacheValue)) {
            return cacheValue;
        }

        String url = String.format(ACCESS_TOKEN_URL, wecomCorp.getCorpId(), wecomCorp.getCorpSecret());
        String response = doGet(url);


        try {
            WecomApiResponse<TokenResponse> apiResponse = objectMapper.readValue(response,
                    objectMapper.getTypeFactory().constructParametricType(WecomApiResponse.class, TokenResponse.class));

            if (apiResponse.getErrcode() != null && apiResponse.getErrcode() == 0) {
                String accessToken = apiResponse.getAccessToken();
                redisCacheService.set(cacheKey, accessToken, 7200, TimeUnit.SECONDS);
                return accessToken;
            } else {
                throw new CommonException("获取access_token失败: " + apiResponse.getErrmsg());
            }
        } catch (IOException e) {
            log.error("解析access_token响应失败", e);
            throw new CommonException("解析access_token响应失败: " + e.getMessage());
        }
    }

    @Override
    public WecomDepartmentListResponse getDepartmentList(WecomCorp wecomCorp, String accessToken, String departmentId) throws CommonException {
        String url;
        if (StringUtils.isNotBlank(departmentId)) {
            url = String.format(DEPARTMENT_LIST_URL, accessToken, departmentId);
        } else {
            url = String.format(DEPARTMENT_LIST_URL, accessToken, "");
        }

        String response = doGet(url);
        try {
            return objectMapper.readValue(response, WecomDepartmentListResponse.class);
        } catch (IOException e) {
            log.error("解析部门列表响应失败", e);
            throw new CommonException("解析部门列表响应失败: " + e.getMessage());
        }
    }

    @Override
    public WecomEmployeeListResponse getEmployeeList(WecomCorp wecomCorp, String accessToken, String departmentId, Boolean fetchChild) throws CommonException {
        if (StringUtils.isBlank(departmentId)) {
            throw new CommonException("departmentId不能为空");
        }
        if (fetchChild == null) {
            fetchChild = false;
        }

        String url = String.format(EMPLOYEE_LIST_URL, accessToken, departmentId, fetchChild ? 1 : 0);
        String response = doGet(url);

        try {
            return objectMapper.readValue(response, WecomEmployeeListResponse.class);
        } catch (IOException e) {
            log.error("解析员工列表响应失败", e);
            throw new CommonException("解析员工列表响应失败: " + e.getMessage());
        }
    }

    @Override
    public WecomEmployeeDetailResponse getEmployeeDetail(WecomCorp wecomCorp, String accessToken, String userid) throws CommonException {
        if (StringUtils.isBlank(userid)) {
            throw new CommonException("userid不能为空");
        }

        String url = String.format(EMPLOYEE_DETAIL_URL, accessToken, userid);
        String response = doGet(url);

        try {
            return objectMapper.readValue(response, WecomEmployeeDetailResponse.class);
        } catch (IOException e) {
            log.error("解析员工详情响应失败", e);
            throw new CommonException("解析员工详情响应失败: " + e.getMessage());
        }
    }

    @Override
    public String doGet(String url) throws CommonException {
        try {
            log.info("企业微信API GET请求: {}", url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept-Charset", StandardCharsets.UTF_8.name());

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            String body = response.getBody();
            log.info("企业微信API响应: {}", body);
            return body;
        } catch (Exception e) {
            log.error("企业微信API GET请求失败: {}", url, e);
            throw new CommonException("企业微信API请求失败: " + e.getMessage());
        }
    }

    @Override
    public String doPost(String url, String requestBody) throws CommonException {
        try {
            log.info("企业微信API POST请求: {}, Body: {}", url, requestBody);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept-Charset", StandardCharsets.UTF_8.name());

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String body = response.getBody();
            log.info("企业微信API响应: {}", body);
            return body;
        } catch (Exception e) {
            log.error("企业微信API POST请求失败: {}", url, e);
            throw new CommonException("企业微信API请求失败: " + e.getMessage());
        }
    }

    /**
     * Token响应内部类
     */
    @lombok.Data
    private static class TokenResponse {
        private String access_token;
        private Integer expires_in;
    }

    // ==================== 外部联系人相关API实现 ====================

    @Override
    public WecomExternalContactListResponse getExternalContactList(WecomCorp wecomCorp, String accessToken, String userid) throws CommonException {
        if (StringUtils.isBlank(userid)) {
            throw new CommonException("userid不能为空");
        }

        String url = String.format(EXTERNAL_CONTACT_LIST_URL, accessToken, userid);
        String response = doGet(url);

        try {
            return objectMapper.readValue(response, WecomExternalContactListResponse.class);
        } catch (IOException e) {
            log.error("解析外部联系人列表响应失败", e);
            throw new CommonException("解析外部联系人列表响应失败: " + e.getMessage());
        }
    }

    @Override
    public WecomExternalContactDetailListResponse getExternalContactDetailList(WecomCorp wecomCorp, String accessToken, String userid, String externalUserid) throws CommonException {
        if (StringUtils.isBlank(userid)) {
            throw new CommonException("userid不能为空");
        }

        // 构建请求体
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"userid\":\"").append(userid).append("\"");
        if (StringUtils.isNotBlank(externalUserid)) {
            // 如果指定了外部联系人userid
            jsonBuilder.append(",\"external_userid_list\":[\"").append(externalUserid).append("\"]");
        }
        jsonBuilder.append("}");

        String url = String.format(EXTERNAL_CONTACT_DETAIL_LIST_URL, accessToken);
        String response = doPost(url, jsonBuilder.toString());

        try {
            return objectMapper.readValue(response, WecomExternalContactDetailListResponse.class);
        } catch (IOException e) {
            log.error("解析外部联系人详情列表响应失败", e);
            throw new CommonException("解析外部联系人详情列表响应失败: " + e.getMessage());
        }
    }

    // ==================== 客户群相关API实现 ====================

    @Override
    public WecomCustomerGroupListResponse getCustomerGroupList(WecomCorp wecomCorp, String accessToken, Integer status, String userId, String cursor, Integer limit) throws CommonException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(String.format(CUSTOMER_GROUP_LIST_URL, accessToken));

        if (status != null) {
            urlBuilder.append("&status=").append(status);
        }
        if (StringUtils.isNotBlank(userId)) {
            urlBuilder.append("&owner_filter=").append(userId);
        }
        if (StringUtils.isNotBlank(cursor)) {
            urlBuilder.append("&cursor=").append(cursor);
        }
        if (limit != null) {
            urlBuilder.append("&limit=").append(limit);
        } else {
            urlBuilder.append("&limit=1000");
        }

        String response = doGet(urlBuilder.toString());

        try {
            return objectMapper.readValue(response, WecomCustomerGroupListResponse.class);
        } catch (IOException e) {
            log.error("解析客户群列表响应失败", e);
            throw new CommonException("解析客户群列表响应失败: " + e.getMessage());
        }
    }

    @Override
    public WecomGroupDetailResponse getGroupDetail(WecomCorp wecomCorp, String accessToken, String chatId) throws CommonException {
        if (StringUtils.isBlank(chatId)) {
            throw new CommonException("chatId不能为空");
        }

        // 构建请求体
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"chat_id\":\"").append(chatId).append("\"}");

        String url = String.format(GROUP_DETAIL_URL, accessToken);
        String response = doPost(url, jsonBuilder.toString());

        try {
            return objectMapper.readValue(response, WecomGroupDetailResponse.class);
        } catch (IOException e) {
            log.error("解析客户群详情响应失败", e);
            throw new CommonException("解析客户群详情响应失败: " + e.getMessage());
        }
    }

    // ==================== 标签相关API实现 ====================

    @Override
    public WecomTagGroupListResponse getCorpTagList(WecomCorp wecomCorp, String accessToken) throws CommonException {
        String url = String.format(CORP_TAG_LIST_URL, accessToken);
        String response = doGet(url);

        try {
            return objectMapper.readValue(response, WecomTagGroupListResponse.class);
        } catch (IOException e) {
            log.error("解析企业标签库响应失败", e);
            throw new CommonException("解析企业标签库响应失败: " + e.getMessage());
        }
    }
}