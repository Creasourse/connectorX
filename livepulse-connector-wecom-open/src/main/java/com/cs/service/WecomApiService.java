package com.cs.service;

import com.cs.dto.*;
import com.cs.entity.WecomCorp;
import com.cs.exception.CommonException;

/**
 * @ClassName: WecomApiService
 * @Author: wwd
 * @TODO: 企业微信API调用服务
 * @Date: 2026/3/21
 */
public interface WecomApiService {

    /**
     * 获取access_token
     *
     * @param wecomCorp 企业微信账户
     * @return access_token
     * @throws CommonException 异常
     */
    String getAccessToken(WecomCorp wecomCorp) throws CommonException;

    /**
     * 获取部门列表
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @param departmentId 部门ID，为空时获取全量组织架构
     * @return 部门列表
     * @throws CommonException 异常
     */
    WecomDepartmentListResponse getDepartmentList(WecomCorp wecomCorp, String accessToken, String departmentId) throws CommonException;

    /**
     * 获取部门成员列表
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @param departmentId 部门ID
     * @param fetchChild 是否递归获取子部门成员
     * @return 员工列表
     * @throws CommonException 异常
     */
    WecomEmployeeListResponse getEmployeeList(WecomCorp wecomCorp, String accessToken, String departmentId, Boolean fetchChild) throws CommonException;

    /**
     * 获取成员详细信息
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @param userid 成员UserID
     * @return 成员详细信息
     * @throws CommonException 异常
     */
    WecomEmployeeDetailResponse getEmployeeDetail(WecomCorp wecomCorp, String accessToken, String userid) throws CommonException;

    /**
     * HTTP GET请求
     *
     * @param url 请求URL
     * @return 响应字符串
     * @throws CommonException 异常
     */
    String doGet(String url) throws CommonException;

    /**
     * HTTP POST请求
     *
     * @param url 请求URL
     * @param requestBody 请求体
     * @return 响应字符串
     * @throws CommonException 异常
     */
    String doPost(String url, String requestBody) throws CommonException;

    // ==================== 外部联系人相关API ====================

    /**
     * 获取客户列表
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @param userid 成员UserID
     * @return 外部联系人列表
     * @throws CommonException 异常
     */
    WecomExternalContactListResponse getExternalContactList(WecomCorp wecomCorp, String accessToken, String userid) throws CommonException;

    /**
     * 批量获取客户详情
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @param userid 成员UserID
     * @param externalUserid 外部联系人userid列表
     * @return 外部联系人详情列表
     * @throws CommonException 异常
     */
    WecomExternalContactDetailListResponse getExternalContactDetailList(WecomCorp wecomCorp, String accessToken, String userid, String externalUserid) throws CommonException;

    // ==================== 客户群相关API ====================

    /**
     * 获取客户群列表
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @param status 群状态，0-全部，1-跟进中，2-离职继承中
     * @param userId 群主ID
     * @param cursor 分页游标
     * @param limit 每页数量
     * @return 客户群列表
     * @throws CommonException 异常
     */
    WecomCustomerGroupListResponse getCustomerGroupList(WecomCorp wecomCorp, String accessToken, Integer status, String userId, String cursor, Integer limit) throws CommonException;

    /**
     * 获取客户群详情
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @param chatId 客户群ID
     * @return 客户群详情
     * @throws CommonException 异常
     */
    WecomGroupDetailResponse getGroupDetail(WecomCorp wecomCorp, String accessToken, String chatId) throws CommonException;

    // ==================== 标签相关API ====================

    /**
     * 获取企业标签库
     *
     * @param wecomCorp 企业微信账户
     * @param accessToken 访问令牌
     * @return 标签组列表
     * @throws CommonException 异常
     */
    WecomTagGroupListResponse getCorpTagList(WecomCorp wecomCorp, String accessToken) throws CommonException;
}