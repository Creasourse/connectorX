package com.cs.controller;

import com.cs.param.WecomSyncLogPageParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.WecomCustomerGroupSyncService;
import com.cs.service.WecomExternalContactSyncService;
import com.cs.service.WecomSyncLogService;
import com.cs.service.WecomSyncService;
import com.cs.service.WecomTagSyncService;
import com.cs.vo.WecomSyncLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: WecomSyncController
 * @Author: wwd
 * @TODO: 企业微信数据同步控制器
 * @Date: 2026/3/21
 */
@Slf4j
@RestController
@RequestMapping("/wecomSync")
@Tag(name = "企业微信数据同步")
public class
WecomSyncController {

    @Autowired
    private WecomSyncService wecomSyncService;

    @Autowired
    private WecomExternalContactSyncService wecomExternalContactSyncService;

    @Autowired
    private WecomCustomerGroupSyncService wecomCustomerGroupSyncService;

    @Autowired
    private WecomTagSyncService wecomTagSyncService;

    @Autowired
    private WecomSyncLogService wecomSyncLogService;

    @PostMapping("/syncDepartments/{wecomCorpId}")
    @Operation(summary = "同步部门列表")
    public RespResult<String> syncDepartments(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomSyncService.syncDepartments(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步部门失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/syncEmployees/{wecomCorpId}")
    @Operation(summary = "同步员工列表")
    public RespResult<String> syncEmployees(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomSyncService.syncEmployees(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步员工失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/syncEmployeeDetails/{wecomCorpId}")
    @Operation(summary = "同步员工详细信息")
    public RespResult<String> syncEmployeeDetails(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomSyncService.syncEmployeeDetails(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步员工详情失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/syncAll/{wecomCorpId}")
    @Operation(summary = "全量同步（部门+员工+员工详情）")
    public RespResult<String> syncAll(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomSyncService.syncAll(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("全量同步失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    // ==================== 外部联系人同步接口 ====================

    @PostMapping("/syncExternalContacts/{wecomCorpId}")
    @Operation(summary = "同步外部联系人列表")
    public RespResult<String> syncExternalContacts(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomExternalContactSyncService.syncExternalContacts(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步外部联系人列表失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/syncExternalContactDetails/{wecomCorpId}")
    @Operation(summary = "同步外部联系人详情")
    public RespResult<String> syncExternalContactDetails(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomExternalContactSyncService.syncExternalContactDetails(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步外部联系人详情失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/syncAllExternalContacts/{wecomCorpId}")
    @Operation(summary = "全量同步外部联系人（列表+详情）")
    public RespResult<String> syncAllExternalContacts(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomExternalContactSyncService.syncAllExternalContacts(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("全量同步外部联系人失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    // ==================== 客户群同步接口 ====================

    @PostMapping("/syncCustomerGroups/{wecomCorpId}")
    @Operation(summary = "同步客户群列表")
    public RespResult<String> syncCustomerGroups(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomCustomerGroupSyncService.syncCustomerGroups(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步客户群列表失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/syncCustomerGroupDetails/{wecomCorpId}")
    @Operation(summary = "同步客户群详情")
    public RespResult<String> syncCustomerGroupDetails(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomCustomerGroupSyncService.syncCustomerGroupDetails(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步客户群详情失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/syncAllCustomerGroups/{wecomCorpId}")
    @Operation(summary = "全量同步客户群（列表+详情）")
    public RespResult<String> syncAllCustomerGroups(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomCustomerGroupSyncService.syncAllCustomerGroups(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("全量同步客户群失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    // ==================== 标签同步接口 ====================

    @PostMapping("/syncCorpTags/{wecomCorpId}")
    @Operation(summary = "同步企业标签库")
    public RespResult<String> syncCorpTags(@PathVariable("wecomCorpId") Long wecomCorpId) {
        try {
            String result = wecomTagSyncService.syncCorpTags(wecomCorpId);
            return RespResult.success(result);
        } catch (Exception e) {
            log.error("同步企业标签失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    // ==================== 同步日志查询接口 ====================

    @PostMapping("/log/pageList")
    @Operation(summary = "分页查询同步日志")
    public RespResult<PageResult<WecomSyncLogVO>> pageSyncLogList(@RequestBody WecomSyncLogPageParam pageParam) {
        try {
            return RespResult.success(wecomSyncLogService.pageList(pageParam));
        } catch (Exception e) {
            log.error("分页查询同步日志失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @GetMapping("/log/detail/{id}")
    @Operation(summary = "查询同步日志详情")
    public RespResult<WecomSyncLogVO> getSyncLogDetail(@PathVariable("id") Long id) {
        try {
            return RespResult.success(wecomSyncLogService.getDetailById(id));
        } catch (Exception e) {
            log.error("查询同步日志详情失败", e);
            return RespResult.failure(e.getMessage());
        }
    }
}