package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.dto.WecomCorpTagDto;
import com.cs.dto.WecomTagGroupDto;
import com.cs.dto.WecomTagGroupListResponse;
import com.cs.entity.WecomCorp;
import com.cs.entity.WecomCorpTags;
import com.cs.entity.WecomSyncLog;
import com.cs.entity.WecomTagGroups;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomCorpTagsMapper;
import com.cs.mapper.WecomSyncLogMapper;
import com.cs.mapper.WecomTagGroupsMapper;
import com.cs.service.WecomApiService;
import com.cs.service.WecomCorpService;
import com.cs.service.WecomTagSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: WecomTagSyncServiceImpl
 * @Author: wwd
 * @TODO: 企业微信标签数据同步服务实现
 * @Date: 2026/3/21
 */
@Slf4j
@Service
public class WecomTagSyncServiceImpl implements WecomTagSyncService {

    @Autowired
    private WecomApiService wecomApiService;

    @Autowired
    private WecomCorpService wecomCorpService;

    @Autowired
    private WecomTagGroupsMapper wecomTagGroupsMapper;

    @Autowired
    private WecomCorpTagsMapper wecomCorpTagsMapper;

    @Autowired
    private WecomSyncLogMapper wecomSyncLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncCorpTags(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "CORP_TAG");

        try {
            // 1. 获取企业微信配置
            WecomCorp wecomCorp = wecomCorpService.getById(wecomCorpId);
            if (wecomCorp == null) {
                throw new CommonException("企业微信账户不存在");
            }

            // 2. 获取access_token
            String accessToken = wecomApiService.getAccessToken(wecomCorp);

            // 3. 获取企业标签库
            WecomTagGroupListResponse response = wecomApiService.getCorpTagList(wecomCorp, accessToken);

            if (response.getErrcode() != null && response.getErrcode() != 0) {
                throw new CommonException("获取企业标签库失败: " + response.getErrmsg());
            }

            List<WecomTagGroupDto> tagGroups = response.getTagGroup();
            if (tagGroups == null || tagGroups.isEmpty()) {
                syncLog.setSyncStatus("SUCCESS");
                syncLog.setEndTime(LocalDateTime.now());
                wecomSyncLogMapper.updateById(syncLog);
                return "企业标签库为空";
            }

            // 4. 保存标签组和标签
            int groupCount = 0;
            int tagCount = 0;
            List<String> allTagIds = new ArrayList<>();

            for (WecomTagGroupDto tagGroupDto : tagGroups) {
                try {
                    // 保存标签组
                    WecomTagGroups tagGroup = saveTagGroup(tagGroupDto, wecomCorpId);
                    groupCount++;

                    // 保存标签
                    if (tagGroupDto.getTag() != null && !tagGroupDto.getTag().isEmpty()) {
                        for (WecomCorpTagDto tagDto : tagGroupDto.getTag()) {
                            try {
                                WecomCorpTags tag = saveCorpTag(tagDto, tagGroupDto.getGroupId(), tagGroupDto.getGroupName(), wecomCorpId);
                                allTagIds.add(tag.getTagId());
                                tagCount++;
                            } catch (Exception e) {
                                log.error("保存标签失败: {}", tagDto.getName(), e);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("保存标签组失败: {}", tagGroupDto.getGroupName(), e);
                }
            }

            // 5. 删除不在本次同步中的标签（软删除）
            LambdaQueryWrapper<WecomCorpTags> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(WecomCorpTags::getWecomCorpId, wecomCorpId)
                    .eq(WecomCorpTags::getDeleted, 0);
            List<WecomCorpTags> allTags = wecomCorpTagsMapper.selectList(deleteWrapper);
            for (WecomCorpTags tag : allTags) {
                if (!allTagIds.contains(tag.getTagId())) {
                    tag.setDeleted(1);
                    tag.setUpdateTime(LocalDateTime.now());
                    wecomCorpTagsMapper.updateById(tag);
                }
            }

            // 6. 更新同步日志
            syncLog.setTotalCount(tagCount);
            syncLog.setSuccessCount(tagCount);
            syncLog.setFailCount(0);
            syncLog.setSyncStatus("SUCCESS");
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);

            return String.format("企业标签同步完成，标签组: %d，标签: %d", groupCount, tagCount);
        } catch (Exception e) {
            log.error("同步企业标签失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步企业标签失败: " + e.getMessage());
        }
    }

    /**
     * 保存标签组
     */
    private WecomTagGroups saveTagGroup(WecomTagGroupDto tagGroupDto, Long wecomCorpId) {
        // 查找是否已存在
        LambdaQueryWrapper<WecomTagGroups> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WecomTagGroups::getGroupId, tagGroupDto.getGroupId());
        WecomTagGroups existingGroup = wecomTagGroupsMapper.selectOne(queryWrapper);

        WecomTagGroups tagGroup = new WecomTagGroups();
        tagGroup.setWecomCorpId(wecomCorpId);
        tagGroup.setGroupId(tagGroupDto.getGroupId());
        tagGroup.setGroupName(tagGroupDto.getGroupName());
        tagGroup.setGroupOrder(tagGroupDto.getOrder() != null ? tagGroupDto.getOrder().intValue() : 0);
        tagGroup.setDeleted(0);

        if (existingGroup != null) {
            tagGroup.setId(existingGroup.getId());
            tagGroup.setUpdateTime(LocalDateTime.now());
            wecomTagGroupsMapper.updateById(tagGroup);
        } else {
            tagGroup.setCreateTime(System.currentTimeMillis() / 1000);
            tagGroup.setUpdateTime(LocalDateTime.now());
            wecomTagGroupsMapper.insert(tagGroup);
        }

        return tagGroup;
    }

    /**
     * 保存企业标签
     */
    private WecomCorpTags saveCorpTag(WecomCorpTagDto tagDto, String groupId, String groupName, Long wecomCorpId) {
        // 查找是否已存在
        LambdaQueryWrapper<WecomCorpTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WecomCorpTags::getTagId, tagDto.getId());
        WecomCorpTags existingTag = wecomCorpTagsMapper.selectOne(queryWrapper);

        WecomCorpTags tag = new WecomCorpTags();
        tag.setWecomCorpId(wecomCorpId);
        tag.setTagId(tagDto.getId());
        tag.setTagName(tagDto.getName());
        tag.setTagType(tagDto.getType() != null ? String.valueOf(tagDto.getType()) : null);
        tag.setGroupId(groupId);
        tag.setGroupName(groupName);
        tag.setTagOrder(tagDto.getOrder() != null ? tagDto.getOrder().intValue() : 0);
        tag.setDeleted(0);

        if (existingTag != null) {
            tag.setId(existingTag.getId());
            tag.setUpdateTime(LocalDateTime.now());
            wecomCorpTagsMapper.updateById(tag);
        } else {
            tag.setCreateTime(System.currentTimeMillis() / 1000);
            tag.setUpdateTime(LocalDateTime.now());
            wecomCorpTagsMapper.insert(tag);
        }

        return tag;
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