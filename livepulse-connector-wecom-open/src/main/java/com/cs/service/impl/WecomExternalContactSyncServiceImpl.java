package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.dto.WecomExternalContactDetailListResponse;
import com.cs.dto.WecomExternalContactDto;
import com.cs.dto.WecomExternalContactListResponse;
import com.cs.entity.*;
import com.cs.exception.CommonException;
import com.cs.mapper.*;
import com.cs.service.WecomApiService;
import com.cs.service.WecomCorpService;
import com.cs.service.WecomExternalContactSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName: WecomExternalContactSyncServiceImpl
 * @Author: wwd
 * @TODO: 企业微信外部联系人数据同步服务实现
 * @Date: 2026/3/21
 */
@Slf4j
@Service
public class WecomExternalContactSyncServiceImpl implements WecomExternalContactSyncService {

    @Autowired
    private WecomApiService wecomApiService;

    @Autowired
    private WecomCorpService wecomCorpService;

    @Autowired
    private WecomEmployeeMapper wecomEmployeeMapper;

    @Autowired
    private WecomExternalContactsMappingMapper wecomExternalContactsMappingMapper;

    @Autowired
    private WecomExternalContactsDetailsMapper wecomExternalContactsDetailsMapper;

    @Autowired
    private WecomExternalTagsMapper wecomExternalTagsMapper;

    @Autowired
    private WecomSyncLogMapper wecomSyncLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncExternalContacts(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "EXTERNAL_CONTACT");

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

            // 4. 遍历员工获取外部联系人
            int totalCount = 0;
            int successCount = 0;
            int failCount = 0;
            List<String> allMappingKeys = new ArrayList<>();

            for (WecomEmployee employee : employees) {
                try {
                    WecomExternalContactListResponse response = wecomApiService.getExternalContactList(
                            wecomCorp, accessToken, employee.getUserid());

                    if (response.getErrcode() != null && response.getErrcode() != 0) {
                        log.warn("获取员工 {} 的外部联系人失败: {}", employee.getName(), response.getErrmsg());
                        continue;
                    }

                    List<String> externalUserids = response.getExternalUseridList();
                    if (externalUserids != null && !externalUserids.isEmpty()) {
                        totalCount += externalUserids.size();

                        for (String externalUserid : externalUserids) {
                            try {
                                String mappingKey = employee.getUserid() + "_" + externalUserid;

                                // 查找是否已存在
                                LambdaQueryWrapper<WecomExternalContactsMapping> queryWrapper = new LambdaQueryWrapper<>();
                                queryWrapper.eq(WecomExternalContactsMapping::getUserid, employee.getUserid())
                                        .eq(WecomExternalContactsMapping::getExternalUserid, externalUserid);
                                WecomExternalContactsMapping existingMapping = wecomExternalContactsMappingMapper.selectOne(queryWrapper);

                                if (existingMapping == null) {
                                    WecomExternalContactsMapping mapping = new WecomExternalContactsMapping();
                                    mapping.setWecomCorpId(wecomCorpId);
                                    mapping.setUserid(employee.getUserid());
                                    mapping.setExternalUserid(externalUserid);
                                    mapping.setDeleted(0);
                                    mapping.setOperateTime(LocalDateTime.now());
                                    mapping.setUpdateTime(LocalDateTime.now());
                                    wecomExternalContactsMappingMapper.insert(mapping);
                                } else {
                                    existingMapping.setDeleted(0);
                                    existingMapping.setUpdateTime(LocalDateTime.now());
                                    wecomExternalContactsMappingMapper.updateById(existingMapping);
                                }

                                allMappingKeys.add(mappingKey);
                                successCount++;
                            } catch (Exception e) {
                                log.error("保存外部联系人映射失败: {}", externalUserid, e);
                                failCount++;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("处理员工 {} 的外部联系人失败", employee.getName(), e);
                }
            }

            // 5. 删除不在本次同步中的映射（软删除）
            LambdaQueryWrapper<WecomExternalContactsMapping> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(WecomExternalContactsMapping::getWecomCorpId, wecomCorpId)
                    .eq(WecomExternalContactsMapping::getDeleted, 0);
            List<WecomExternalContactsMapping> allMappings = wecomExternalContactsMappingMapper.selectList(deleteWrapper);
            for (WecomExternalContactsMapping mapping : allMappings) {
                String mappingKey = mapping.getUserid() + "_" + mapping.getExternalUserid();
                if (!allMappingKeys.contains(mappingKey)) {
                    mapping.setDeleted(1);
                    mapping.setUpdateTime(LocalDateTime.now());
                    wecomExternalContactsMappingMapper.updateById(mapping);
                }
            }

            // 6. 更新同步日志
            syncLog.setTotalCount(totalCount);
            syncLog.setSuccessCount(successCount);
            syncLog.setFailCount(failCount);
            syncLog.setSyncStatus(failCount > 0 ? "PARTIAL" : "SUCCESS");
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);

            return String.format("外部联系人映射同步完成，总数: %d，成功: %d，失败: %d", totalCount, successCount, failCount);
        } catch (Exception e) {
            log.error("同步外部联系人失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步外部联系人失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncExternalContactDetails(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "EXTERNAL_CONTACT_DETAIL");

        try {
            // 1. 获取企业微信配置
            WecomCorp wecomCorp = wecomCorpService.getById(wecomCorpId);
            if (wecomCorp == null) {
                throw new CommonException("企业微信账户不存在");
            }

            // 2. 获取access_token
            String accessToken = wecomApiService.getAccessToken(wecomCorp);

            // 3. 获取所有外部联系人映射
            LambdaQueryWrapper<WecomExternalContactsMapping> mappingQueryWrapper = new LambdaQueryWrapper<>();
            mappingQueryWrapper.eq(WecomExternalContactsMapping::getWecomCorpId, wecomCorpId)
                    .eq(WecomExternalContactsMapping::getDeleted, 0);
            List<WecomExternalContactsMapping> mappings = wecomExternalContactsMappingMapper.selectList(mappingQueryWrapper);

            if (mappings == null || mappings.isEmpty()) {
                throw new CommonException("没有可用的外部联系人映射，请先同步外部联系人列表");
            }

            // 4. 按员工分组获取外部联系人详情
            AtomicInteger totalCount = new AtomicInteger();
            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failCount = new AtomicInteger();

            // 按userid分组
            mappings.stream()
                    .collect(Collectors.groupingBy(WecomExternalContactsMapping::getUserid))
                    .forEach((userid, userMappings) -> {
                        try {
                            WecomExternalContactDetailListResponse response = wecomApiService.getExternalContactDetailList(
                                    wecomCorp, accessToken, userid, null);

                            if (response.getErrcode() != null && response.getErrcode() != 0) {
                                log.warn("获取员工 {} 的外部联系人详情失败: {}", userid, response.getErrmsg());
                                return;
                            }

                            List<WecomExternalContactDetailListResponse.ExternalContactItem> items = response.getExternalContactList();
                            if (items != null && !items.isEmpty()) {
                                for (WecomExternalContactDetailListResponse.ExternalContactItem item : items) {
                                    try {
                                        // 保存外部联系人详情
                                        saveExternalContactDetail(item, wecomCorpId);

                                        // 保存标签
                                        if (item.getTags() != null && !item.getTags().isEmpty()) {
                                            saveExternalTags(item, userid);
                                        }

                                        successCount.getAndIncrement();
                                    } catch (Exception e) {
                                        log.error("保存外部联系人详情失败: {}", item.getExternalContact().getName(), e);
                                        failCount.getAndIncrement();
                                    }
                                    totalCount.getAndIncrement();
                                }
                            }
                        } catch (Exception e) {
                            log.error("处理员工 {} 的外部联系人详情失败", userid, e);
                        }
                    });

            // 5. 更新同步日志
            syncLog.setTotalCount(totalCount.get());
            syncLog.setSuccessCount(successCount.get());
            syncLog.setFailCount(failCount.get());
            syncLog.setSyncStatus(failCount.get() > 0 ? "PARTIAL" : "SUCCESS");
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);

            return String.format("外部联系人详情同步完成，总数: %d，成功: %d，失败: %d", totalCount, successCount, failCount);
        } catch (Exception e) {
            log.error("同步外部联系人详情失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步外部联系人详情失败: " + e.getMessage());
        }
    }

    @Override
    public String syncAllExternalContacts(Long wecomCorpId) throws CommonException {
        StringBuilder result = new StringBuilder();

        result.append("开始全量同步外部联系人...\n");

        // 1. 同步外部联系人列表
        result.append(syncExternalContacts(wecomCorpId)).append("\n");

        // 2. 同步外部联系人详情
        result.append(syncExternalContactDetails(wecomCorpId)).append("\n");

        result.append("外部联系人全量同步完成");

        return result.toString();
    }

    /**
     * 保存外部联系人详情
     */
    private void saveExternalContactDetail(WecomExternalContactDetailListResponse.ExternalContactItem item, Long wecomCorpId) {
        WecomExternalContactDto contactDto = item.getExternalContact();

        // 查找是否已存在
        LambdaQueryWrapper<WecomExternalContactsDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WecomExternalContactsDetails::getExternalUserid, contactDto.getExternalUserid());
        WecomExternalContactsDetails existingDetail = wecomExternalContactsDetailsMapper.selectOne(queryWrapper);

        WecomExternalContactsDetails detail = new WecomExternalContactsDetails();
        detail.setExternalUserid(contactDto.getExternalUserid());
        detail.setName(contactDto.getName());
        detail.setPosition(contactDto.getPosition());
        detail.setAvatar(contactDto.getAvatar());
        detail.setType(contactDto.getType());
        detail.setGender(contactDto.getGender());
        detail.setUnionid(contactDto.getUnionid());
        detail.setCorpName(contactDto.getCorpName());
        detail.setCorpFullName(contactDto.getCorpFullName());
        detail.setFollowUser(item.getFollowUser());
        detail.setAddTime(item.getAddTime());
        detail.setDeleted(0);

        if (existingDetail != null) {
            detail.setId(existingDetail.getId());
            detail.setUpdateTime(LocalDateTime.now());
            wecomExternalContactsDetailsMapper.updateById(detail);
        } else {
            detail.setUpdateTime(LocalDateTime.now());
            wecomExternalContactsDetailsMapper.insert(detail);
        }

        // 同时更新映射表的备注信息
        LambdaQueryWrapper<WecomExternalContactsMapping> mappingWrapper = new LambdaQueryWrapper<>();
        mappingWrapper.eq(WecomExternalContactsMapping::getUserid, item.getFollowUser())
                .eq(WecomExternalContactsMapping::getExternalUserid, contactDto.getExternalUserid());
        WecomExternalContactsMapping mapping = wecomExternalContactsMappingMapper.selectOne(mappingWrapper);
        if (mapping != null) {
            mapping.setRemark(item.getRemark());
            mapping.setDescription(item.getDescription());
            mapping.setRemarkCorpName(item.getRemarkCompany());
            if (item.getRemarkMobiles() != null && !item.getRemarkMobiles().isEmpty()) {
                mapping.setRemarkMobiles(String.join(",", item.getRemarkMobiles()));
            }
            mapping.setUpdateTime(LocalDateTime.now());
            wecomExternalContactsMappingMapper.updateById(mapping);
        }
    }

    /**
     * 保存外部联系人标签
     */
    private void saveExternalTags(WecomExternalContactDetailListResponse.ExternalContactItem item, String userid) {
        // 先删除该联系人的旧标签
        LambdaQueryWrapper<WecomExternalTags> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(WecomExternalTags::getExternalUserid, item.getExternalContact().getExternalUserid())
                .eq(WecomExternalTags::getUserId, userid);
        wecomExternalTagsMapper.delete(deleteWrapper);

        // 添加新标签
        for (WecomExternalContactDetailListResponse.TagItem tagItem : item.getTags()) {
            WecomExternalTags tag = new WecomExternalTags();
            tag.setExternalUserid(item.getExternalContact().getExternalUserid());
            tag.setUserId(userid);
            tag.setTagId(tagItem.getTagId());
            tag.setTagName(tagItem.getTagName());
            tag.setTagType(tagItem.getTagType());
            tag.setGroupName(tagItem.getGroupName());
            tag.setCreateTime(System.currentTimeMillis() / 1000);
            tag.setUpdateTime(LocalDateTime.now());
            tag.setDeleted(0);
            wecomExternalTagsMapper.insert(tag);
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