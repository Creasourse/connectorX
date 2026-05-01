package com.cs.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.config.ScheduledTaskProperties;
import com.cs.entity.WecomCorp;
import com.cs.mapper.WecomCorpMapper;
import com.cs.service.WecomCustomerGroupSyncService;
import com.cs.service.WecomExternalContactSyncService;
import com.cs.service.WecomSyncService;
import com.cs.service.WecomTagSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: WecomSyncScheduledTask
 * @Author: wwd
 * @TODO: 企业微信数据同步调度任务
 * @Date: 2026/3/26
 */
@Slf4j
@Component
public class WecomSyncScheduledTask {

    @Autowired
    private ScheduledTaskProperties scheduledTaskProperties;

    @Autowired
    private WecomCorpMapper wecomCorpMapper;

    @Autowired
    private WecomSyncService wecomSyncService;

    @Autowired
    private WecomExternalContactSyncService wecomExternalContactSyncService;

    @Autowired
    private WecomCustomerGroupSyncService wecomCustomerGroupSyncService;

    @Autowired
    private WecomTagSyncService wecomTagSyncService;

    /**
     * 同步部门+员工信息
     * 支持配置文件中的 cron 表达式或 fixedDelay
     */
    @Scheduled(
        cron = "${wecom.scheduled.department-and-employee.cron:}"
//            ,
//        initialDelayString = "${wecom.scheduled.department-and-employee.initial-delay:60}",
//        fixedDelayString = "${wecom.scheduled.department-and-employee.fixed-delay:}"
    )
    public void syncDepartmentAndEmployee() {
        try {
            // 检查是否启用
            if (!isTaskEnabled(scheduledTaskProperties.getDepartmentAndEmployee())) {
                log.debug("部门+员工信息同步任务未启用");
                return;
            }

            log.info("========== 开始执行部门+员工信息同步调度任务 ==========");

            // 获取所有启用的企业微信账户
            List<Long> corpIds = getEnabledCorpIds();
            if (corpIds.isEmpty()) {
                log.warn("没有找到启用中的企业微信账户");
                return;
            }

            for (Long corpId : corpIds) {
                try {
                    log.info("开始同步企业微信账户 [{}] 的部门和员工信息", corpId);

                    // 同步部门
                    String deptResult = wecomSyncService.syncDepartments(corpId);
                    log.info("企业微信账户 [{}] 部门同步结果: {}", corpId, deptResult);

                    // 同步员工
                    String empResult = wecomSyncService.syncEmployees(corpId);
                    log.info("企业微信账户 [{}] 员工同步结果: {}", corpId, empResult);

                    // 同步员工详细信息
                    String empDetailResult = wecomSyncService.syncEmployeeDetails(corpId);
                    log.info("企业微信账户 [{}] 员工详情同步结果: {}", corpId, empDetailResult);

                } catch (Exception e) {
                    log.error("企业微信账户 [{}] 同步部门和员工信息失败", corpId, e);
                }
            }

            log.info("========== 部门+员工信息同步调度任务执行完成 ==========");
        } catch (Exception e) {
            log.error("部门+员工信息同步调度任务执行异常", e);
        }
    }

    /**
     * 同步外部联系人
     * 支持配置文件中的 cron 表达式或 fixedDelay
     */
    @Scheduled(
        cron = "${wecom.scheduled.external-contact.cron:}"
//            ,
//        initialDelayString = "${wecom.scheduled.external-contact.initial-delay:120}",
//        fixedDelayString = "${wecom.scheduled.external-contact.fixed-delay:}"
    )
    public void syncExternalContact() {
        try {
            // 检查是否启用
            if (!isTaskEnabled(scheduledTaskProperties.getExternalContact())) {
                log.debug("外部联系人同步任务未启用");
                return;
            }

            log.info("========== 开始执行外部联系人同步调度任务 ==========");

            // 获取所有启用的企业微信账户
            List<Long> corpIds = getEnabledCorpIds();
            if (corpIds.isEmpty()) {
                log.warn("没有找到启用中的企业微信账户");
                return;
            }

            for (Long corpId : corpIds) {
                try {
                    log.info("开始同步企业微信账户 [{}] 的外部联系人信息", corpId);

                    // 全量同步外部联系人（列表+详情）
                    String result = wecomExternalContactSyncService.syncAllExternalContacts(corpId);
                    log.info("企业微信账户 [{}] 外部联系人同步结果: {}", corpId, result);

                } catch (Exception e) {
                    log.error("企业微信账户 [{}] 同步外部联系人失败", corpId, e);
                }
            }

            log.info("========== 外部联系人同步调度任务执行完成 ==========");
        } catch (Exception e) {
            log.error("外部联系人同步调度任务执行异常", e);
        }
    }

    /**
     * 同步客户群
     * 支持配置文件中的 cron 表达式或 fixedDelay
     */
    @Scheduled(
        cron = "${wecom.scheduled.customer-group.cron:}"
//            ,
//        initialDelayString = "${wecom.scheduled.customer-group.initial-delay:180}",
//        fixedDelayString = "${wecom.scheduled.customer-group.fixed-delay:}"
    )
    public void syncCustomerGroup() {
        try {
            // 检查是否启用
            if (!isTaskEnabled(scheduledTaskProperties.getCustomerGroup())) {
                log.debug("客户群同步任务未启用");
                return;
            }

            log.info("========== 开始执行客户群同步调度任务 ==========");

            // 获取所有启用的企业微信账户
            List<Long> corpIds = getEnabledCorpIds();
            if (corpIds.isEmpty()) {
                log.warn("没有找到启用中的企业微信账户");
                return;
            }

            for (Long corpId : corpIds) {
                try {
                    log.info("开始同步企业微信账户 [{}] 的客户群信息", corpId);

                    // 全量同步客户群（列表+详情）
                    String result = wecomCustomerGroupSyncService.syncAllCustomerGroups(corpId);
                    log.info("企业微信账户 [{}] 客户群同步结果: {}", corpId, result);

                } catch (Exception e) {
                    log.error("企业微信账户 [{}] 同步客户群失败", corpId, e);
                }
            }

            log.info("========== 客户群同步调度任务执行完成 ==========");
        } catch (Exception e) {
            log.error("客户群同步调度任务执行异常", e);
        }
    }

    /**
     * 同步企业标签库
     * 支持配置文件中的 cron 表达式或 fixedDelay
     */
    @Scheduled(
        cron = "${wecom.scheduled.corp-tag.cron:}"
//            ,
//        initialDelayString = "${wecom.scheduled.corp-tag.initial-delay:240}",
//        fixedDelayString = "${wecom.scheduled.corp-tag.fixed-delay:}"
    )
    public void syncCorpTag() {
        try {
            // 检查是否启用
            if (!isTaskEnabled(scheduledTaskProperties.getCorpTag())) {
                log.debug("企业标签库同步任务未启用");
                return;
            }

            log.info("========== 开始执行企业标签库同步调度任务 ==========");

            // 获取所有启用的企业微信账户
            List<Long> corpIds = getEnabledCorpIds();
            if (corpIds.isEmpty()) {
                log.warn("没有找到启用中的企业微信账户");
                return;
            }

            for (Long corpId : corpIds) {
                try {
                    log.info("开始同步企业微信账户 [{}] 的标签库信息", corpId);

                    // 同步企业标签库
                    String result = wecomTagSyncService.syncCorpTags(corpId);
                    log.info("企业微信账户 [{}] 标签库同步结果: {}", corpId, result);

                } catch (Exception e) {
                    log.error("企业微信账户 [{}] 同步标签库失败", corpId, e);
                }
            }

            log.info("========== 企业标签库同步调度任务执行完成 ==========");
        } catch (Exception e) {
            log.error("企业标签库同步调度任务执行异常", e);
        }
    }

    /**
     * 获取所有启用的企业微信账户ID列表
     *
     * @return 企业微信账户ID列表
     */
    private List<Long> getEnabledCorpIds() {
        try {
            // 查询所有企业微信账户
            // TODO: 根据实际业务需求，可以添加状态字段过滤启用的账户
            LambdaQueryWrapper<WecomCorp> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.isNotNull(WecomCorp::getWecomeCorpId);
            List<WecomCorp> corps = wecomCorpMapper.selectList(queryWrapper);

            List<Long> corpIds = new ArrayList<>();
            if (corps != null && !corps.isEmpty()) {
                for (WecomCorp corp : corps) {
                    corpIds.add(corp.getWecomeCorpId());
                }
            }

            log.info("查询到 {} 个企业微信账户", corpIds.size());
            return corpIds;
        } catch (Exception e) {
            log.error("获取启用中的企业微信账户失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 检查任务是否启用
     *
     * @param syncConfig 同步配置
     * @return 是否启用
     */
    private boolean isTaskEnabled(ScheduledTaskProperties.SyncConfig syncConfig) {
        if (Objects.isNull(scheduledTaskProperties.getEnabled()) || !scheduledTaskProperties.getEnabled()) {
            return false;
        }
        if (Objects.isNull(syncConfig) || Objects.isNull(syncConfig.getEnabled())) {
            return true; // 默认启用
        }
        return syncConfig.getEnabled();
    }
}