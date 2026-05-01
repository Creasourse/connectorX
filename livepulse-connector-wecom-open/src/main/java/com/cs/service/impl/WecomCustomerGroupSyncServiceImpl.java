package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.dto.WecomCustomerGroupListResponse;
import com.cs.dto.WecomGroupDetailResponse;
import com.cs.dto.WecomGroupMemberDto;
import com.cs.entity.*;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomCustomerGroupsMapper;
import com.cs.mapper.WecomEmployeeMapper;
import com.cs.mapper.WecomGroupMembersMapper;
import com.cs.mapper.WecomSyncLogMapper;
import com.cs.service.WecomApiService;
import com.cs.service.WecomCorpService;
import com.cs.service.WecomCustomerGroupSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: WecomCustomerGroupSyncServiceImpl
 * @Author: wwd
 * @TODO: 企业微信客户群数据同步服务实现
 * @Date: 2026/3/21
 */
@Slf4j
@Service
public class WecomCustomerGroupSyncServiceImpl implements WecomCustomerGroupSyncService {

    @Autowired
    private WecomApiService wecomApiService;

    @Autowired
    private WecomCorpService wecomCorpService;

    @Autowired
    private WecomEmployeeMapper wecomEmployeeMapper;

    @Autowired
    private WecomCustomerGroupsMapper wecomCustomerGroupsMapper;

    @Autowired
    private WecomGroupMembersMapper wecomGroupMembersMapper;

    @Autowired
    private WecomSyncLogMapper wecomSyncLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncCustomerGroups(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "CUSTOMER_GROUP");

        try {
            // 1. 获取企业微信配置
            WecomCorp wecomCorp = wecomCorpService.getById(wecomCorpId);
            if (wecomCorp == null) {
                throw new CommonException("企业微信账户不存在");
            }

            // 2. 获取access_token
            String accessToken = wecomApiService.getAccessToken(wecomCorp);

            // 3. 获取所有员工
            LambdaQueryWrapper<WecomEmployee> empQueryWrapper = new LambdaQueryWrapper<>();
            empQueryWrapper.eq(WecomEmployee::getWecomCorpId, wecomCorpId)
                    .eq(WecomEmployee::getDeleted, 0);
            List<WecomEmployee> employees = wecomEmployeeMapper.selectList(empQueryWrapper);

            if (employees == null || employees.isEmpty()) {
                throw new CommonException("没有可用的员工，请先同步员工信息");
            }

            // 4. 遍历员工获取客户群
            int totalCount = 0;
            int successCount = 0;
            int failCount = 0;
            List<String> allChatIds = new ArrayList<>();
            String cursor = null;

            // 获取每个员工的客户群
            for (WecomEmployee employee : employees) {
                try {
                    do {
                        WecomCustomerGroupListResponse response = wecomApiService.getCustomerGroupList(
                                wecomCorp, accessToken, null, employee.getUserid(), cursor, 1000);

                        if (response.getErrcode() != null && response.getErrcode() != 0) {
                            log.warn("获取员工 {} 的客户群失败: {}", employee.getName(), response.getErrmsg());
                            break;
                        }

                        // 处理客户群数据
                        if (response.getChatData() != null && !response.getChatData().isEmpty()) {
                            for (WecomCustomerGroupListResponse.GroupChatData chatData : response.getChatData()) {
                                try {
                                    if (!allChatIds.contains(chatData.getChatId())) {
                                        // 保存客户群基本信息
                                        saveCustomerGroup(chatData, wecomCorpId);
                                        allChatIds.add(chatData.getChatId());
                                        successCount++;
                                    }
                                    totalCount++;
                                } catch (Exception e) {
                                    log.error("保存客户群失败: {}", chatData.getName(), e);
                                    failCount++;
                                }
                            }
                        }

                        // 获取下一页
                        cursor = null; // 企业微信API返回的next_cursor字段
                    } while (cursor != null && !cursor.isEmpty());

                } catch (Exception e) {
                    log.error("处理员工 {} 的客户群失败", employee.getName(), e);
                }
            }

            // 5. 删除不在本次同步中的客户群（软删除）
            LambdaQueryWrapper<WecomCustomerGroups> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(WecomCustomerGroups::getWecomCorpId, wecomCorpId)
                    .eq(WecomCustomerGroups::getDeleted, 0);
            List<WecomCustomerGroups> allGroups = wecomCustomerGroupsMapper.selectList(deleteWrapper);
            for (WecomCustomerGroups group : allGroups) {
                if (!allChatIds.contains(group.getChatId())) {
                    group.setDeleted(1);
                    group.setUpdateTime(LocalDateTime.now());
                    wecomCustomerGroupsMapper.updateById(group);
                }
            }

            // 6. 更新同步日志
            syncLog.setTotalCount(totalCount);
            syncLog.setSuccessCount(successCount);
            syncLog.setFailCount(failCount);
            syncLog.setSyncStatus(failCount > 0 ? "PARTIAL" : "SUCCESS");
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);

            return String.format("客户群同步完成，总数: %d，成功: %d，失败: %d", totalCount, successCount, failCount);
        } catch (Exception e) {
            log.error("同步客户群失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步客户群失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncCustomerGroupDetails(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "CUSTOMER_GROUP_DETAIL");

        try {
            // 1. 获取企业微信配置
            WecomCorp wecomCorp = wecomCorpService.getById(wecomCorpId);
            if (wecomCorp == null) {
                throw new CommonException("企业微信账户不存在");
            }

            // 2. 获取access_token
            String accessToken = wecomApiService.getAccessToken(wecomCorp);

            // 3. 获取所有客户群
            LambdaQueryWrapper<WecomCustomerGroups> groupQueryWrapper = new LambdaQueryWrapper<>();
            groupQueryWrapper.eq(WecomCustomerGroups::getWecomCorpId, wecomCorpId)
                    .eq(WecomCustomerGroups::getDeleted, 0);
            List<WecomCustomerGroups> groups = wecomCustomerGroupsMapper.selectList(groupQueryWrapper);

            if (groups == null || groups.isEmpty()) {
                throw new CommonException("没有可用的客户群，请先同步客户群列表");
            }

            // 4. 遍历客户群获取详情
            int totalCount = groups.size();
            int successCount = 0;
            int failCount = 0;

            for (WecomCustomerGroups group : groups) {
                try {
                    WecomGroupDetailResponse response = wecomApiService.getGroupDetail(
                            wecomCorp, accessToken, group.getChatId());

                    if (response.getErrcode() != null && response.getErrcode() != 0) {
                        log.warn("获取客户群 {} 的详情失败: {}", group.getName(), response.getErrmsg());
                        failCount++;
                        continue;
                    }

                    // 更新客户群信息
                    updateCustomerGroupFromDetail(response, wecomCorpId);

                    // 保存群成员
                    if (response.getMemberList() != null && !response.getMemberList().isEmpty()) {
                        saveGroupMembers(response.getMemberList(), group.getChatId());
                    }

                    successCount++;
                } catch (Exception e) {
                    log.error("保存客户群详情失败: {}", group.getName(), e);
                    failCount++;
                }
            }

            // 5. 更新同步日志
            syncLog.setTotalCount(totalCount);
            syncLog.setSuccessCount(successCount);
            syncLog.setFailCount(failCount);
            syncLog.setSyncStatus(failCount > 0 ? "PARTIAL" : "SUCCESS");
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);

            return String.format("客户群详情同步完成，总数: %d，成功: %d，失败: %d", totalCount, successCount, failCount);
        } catch (Exception e) {
            log.error("同步客户群详情失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步客户群详情失败: " + e.getMessage());
        }
    }

    @Override
    public String syncAllCustomerGroups(Long wecomCorpId) throws CommonException {
        StringBuilder result = new StringBuilder();

        result.append("开始全量同步客户群...\n");

        // 1. 同步客户群列表
        result.append(syncCustomerGroups(wecomCorpId)).append("\n");

        // 2. 同步客户群详情
        result.append(syncCustomerGroupDetails(wecomCorpId)).append("\n");

        result.append("客户群全量同步完成");

        return result.toString();
    }

    /**
     * 保存客户群基本信息
     */
    private void saveCustomerGroup(WecomCustomerGroupListResponse.GroupChatData chatData, Long wecomCorpId) {
        // 查找是否已存在
        LambdaQueryWrapper<WecomCustomerGroups> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WecomCustomerGroups::getChatId, chatData.getChatId());
        WecomCustomerGroups existingGroup = wecomCustomerGroupsMapper.selectOne(queryWrapper);

        WecomCustomerGroups group = new WecomCustomerGroups();
        group.setWecomCorpId(wecomCorpId);
        group.setChatId(chatData.getChatId());
        group.setName(chatData.getName());
        group.setOwner(chatData.getOwner());
        group.setCreateTime(chatData.getCreateTime());
        group.setNotice(chatData.getNotice());
        group.setMemberCount(chatData.getMemberCount());
        group.setAdminList(chatData.getAdminList() != null ? String.join(",", chatData.getAdminList()) : null);
        group.setChatAnnouncement(chatData.getChatAnnouncement());
        group.setGroupQrcode(chatData.getGroupQrcode());
        group.setDeleted(0);

        if (existingGroup != null) {
            group.setId(existingGroup.getId());
            group.setUpdateTime(LocalDateTime.now());
            wecomCustomerGroupsMapper.updateById(group);
        } else {
            group.setUpdateTime(LocalDateTime.now());
            wecomCustomerGroupsMapper.insert(group);
        }
    }

    /**
     * 从详情更新客户群信息
     */
    private void updateCustomerGroupFromDetail(WecomGroupDetailResponse response, Long wecomCorpId) {
        LambdaQueryWrapper<WecomCustomerGroups> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WecomCustomerGroups::getChatId, response.getChatId());
        WecomCustomerGroups group = wecomCustomerGroupsMapper.selectOne(queryWrapper);

        if (group != null) {
            group.setName(response.getName());
            group.setOwner(response.getOwner());
            group.setCreateTime(response.getCreateTime());
            group.setNotice(response.getNotice());
            group.setMemberCount(response.getMemberCount());
            group.setAdminList(response.getAdminList() != null ? String.join(",", response.getAdminList()) : null);
            group.setChatAnnouncement(response.getChatAnnouncement());
            group.setGroupQrcode(response.getGroupQrcode());
            group.setUpdateTime(LocalDateTime.now());
            wecomCustomerGroupsMapper.updateById(group);
        }
    }

    /**
     * 保存群成员
     */
    private void saveGroupMembers(List<WecomGroupMemberDto> memberList, String chatId) {
        // 先删除旧成员
        LambdaQueryWrapper<WecomGroupMembers> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(WecomGroupMembers::getChatId, chatId);
        wecomGroupMembersMapper.delete(deleteWrapper);

        // 添加新成员
        for (WecomGroupMemberDto memberDto : memberList) {
            WecomGroupMembers member = new WecomGroupMembers();
            member.setChatId(chatId);
            member.setMemberId(memberDto.getUserid() != null ? memberDto.getUserid() : memberDto.getUnionid());
            member.setType(memberDto.getType());
            member.setUnionid(memberDto.getUnionid());
            member.setJoinTime(memberDto.getJoinTime());
            member.setJoinScene(memberDto.getJoinScene());
            member.setInvitor(memberDto.getInvitor());
            member.setGroupNickname(memberDto.getGroupNickname());
            member.setName(memberDto.getName());
            member.setUserId(memberDto.getUserid());
            member.setUpdateTime(LocalDateTime.now());
            member.setDeleted(0);
            wecomGroupMembersMapper.insert(member);
        }
    }

    /**
     * 创建同步日志
     */
    private WecomSyncLog createSyncLog(Long wecomCorpId, String syncType) {
        WecomSyncLog syncLog = new WecomSyncLog();
        syncLog.setWecomCorpId(wecomCorpId);
        syncLog.setSyncType(syncType);
        syncLog.setSyncStatus("RUNNING");
        syncLog.setStartTime(LocalDateTime.now());
        syncLog.setTotalCount(0);
        syncLog.setSuccessCount(0);
        syncLog.setFailCount(0);
        syncLog.setCreateTime(LocalDateTime.now());
        wecomSyncLogMapper.insert(syncLog);
        return syncLog;
    }
}