package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.dto.*;
import com.cs.entity.*;
import com.cs.exception.CommonException;
import com.cs.mapper.*;
import com.cs.service.WecomApiService;
import com.cs.service.WecomCorpService;
import com.cs.service.WecomSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: WecomSyncServiceImpl
 * @Author: wwd
 * @TODO: 企业微信数据同步服务实现
 * @Date: 2026/3/21
 */
@Slf4j
@Service
public class WecomSyncServiceImpl implements WecomSyncService {

    @Autowired
    private WecomApiService wecomApiService;

    @Autowired
    private WecomCorpService wecomCorpService;

    @Autowired
    private WecomCorpMapper wecomCorpMapper;

    @Autowired
    private WecomDepartmentMapper wecomDepartmentMapper;

    @Autowired
    private WecomEmployeeMapper wecomEmployeeMapper;

    @Autowired
    private WecomEmployeeDetailMapper wecomEmployeeDetailMapper;

    @Autowired
    private WecomSyncLogMapper wecomSyncLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncDepartments(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "DEPARTMENT");

        try {
            // 1. 获取企业微信配置
            WecomCorp wecomCorp = wecomCorpService.getById(wecomCorpId);
            if (wecomCorp == null) {
                throw new CommonException("企业微信账户不存在");
            }

            // 2. 获取access_token
            String accessToken = wecomApiService.getAccessToken(wecomCorp);

            // 3. 获取部门列表
            WecomDepartmentListResponse response = wecomApiService.getDepartmentList(wecomCorp, accessToken, null);

            if (response.getErrcode() != null && response.getErrcode() != 0) {
                throw new CommonException("获取部门列表失败: " + response.getErrmsg());
            }

            List<WecomDepartmentDto> departmentDtos = response.getDepartment();
            if (departmentDtos == null || departmentDtos.isEmpty()) {
                syncLog.setSyncStatus("SUCCESS");
                syncLog.setEndTime(LocalDateTime.now());
                wecomSyncLogMapper.updateById(syncLog);
                return "部门列表为空";
            }

            // 4. 保存部门信息
            int successCount = 0;
            int failCount = 0;

            // 先标记所有现有部门为已删除
            LambdaQueryWrapper<WecomDepartment> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(WecomDepartment::getWecomCorpId, wecomCorpId);
            List<WecomDepartment> existingDepartments = wecomDepartmentMapper.selectList(updateWrapper);
            for (WecomDepartment dept : existingDepartments) {
                dept.setDeleted(1);
                wecomDepartmentMapper.updateById(dept);
            }

            for (WecomDepartmentDto dto : departmentDtos) {
                try {
                    WecomDepartment department = convertToDepartment(dto, wecomCorpId);

                    // 查找是否已存在
                    LambdaQueryWrapper<WecomDepartment> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(WecomDepartment::getWecomCorpId, wecomCorpId)
                            .eq(WecomDepartment::getDepartmentId, dto.getId());
                    WecomDepartment existingDept = wecomDepartmentMapper.selectOne(queryWrapper);

                    if (existingDept != null) {
                        department.setId(existingDept.getId());
                        department.setDeleted(0);
                        wecomDepartmentMapper.updateById(department);
                    } else {
                        department.setDeleted(0);
                        wecomDepartmentMapper.insert(department);
                    }
                    successCount++;
                } catch (Exception e) {
                    log.error("保存部门失败: {}", dto.getName(), e);
                    failCount++;
                }
            }

            // 5. 更新同步日志
            syncLog.setTotalCount(departmentDtos.size());
            syncLog.setSuccessCount(successCount);
            syncLog.setFailCount(failCount);
            syncLog.setSyncStatus(failCount > 0 ? "PARTIAL" : "SUCCESS");
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);

            return String.format("部门同步完成，总数: %d，成功: %d，失败: %d", departmentDtos.size(), successCount, failCount);
        } catch (Exception e) {
            log.error("同步部门失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步部门失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncEmployees(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "EMPLOYEE");

        try {
            // 1. 获取企业微信配置
            WecomCorp wecomCorp = wecomCorpService.getById(wecomCorpId);
            if (wecomCorp == null) {
                throw new CommonException("企业微信账户不存在");
            }

            // 2. 获取access_token
            String accessToken = wecomApiService.getAccessToken(wecomCorp);

            // 3. 获取所有部门
            LambdaQueryWrapper<WecomDepartment> deptQueryWrapper = new LambdaQueryWrapper<>();
            deptQueryWrapper.eq(WecomDepartment::getWecomCorpId, wecomCorpId)
                    .eq(WecomDepartment::getDeleted, 0);
            List<WecomDepartment> departments = wecomDepartmentMapper.selectList(deptQueryWrapper);

            if (departments == null || departments.isEmpty()) {
                throw new CommonException("没有可用的部门，请先同步部门信息");
            }

            // 4. 遍历部门获取员工
            int totalCount = 0;
            int successCount = 0;
            int failCount = 0;
            List<String> employeeUserids = new ArrayList<>();

            for (WecomDepartment department : departments) {
                try {
                    WecomEmployeeListResponse response = wecomApiService.getEmployeeList(
                            wecomCorp, accessToken, department.getDepartmentId(), true);

                    if (response.getErrcode() != null && response.getErrcode() != 0) {
                        log.warn("获取部门 {} 的员工失败: {}", department.getName(), response.getErrmsg());
                        continue;
                    }

                    List<WecomEmployeeDto> employeeDtos = response.getUserlist();
                    if (employeeDtos != null && !employeeDtos.isEmpty()) {
                        totalCount += employeeDtos.size();

                        for (WecomEmployeeDto dto : employeeDtos) {
                            if (!employeeUserids.contains(dto.getUserid())) {
                                try {
                                    WecomEmployee employee = convertToEmployee(dto, wecomCorpId);

                                    // 查找是否已存在
                                    LambdaQueryWrapper<WecomEmployee> queryWrapper = new LambdaQueryWrapper<>();
                                    queryWrapper.eq(WecomEmployee::getWecomCorpId, wecomCorpId)
                                            .eq(WecomEmployee::getUserid, dto.getUserid());
                                    WecomEmployee existingEmployee = wecomEmployeeMapper.selectOne(queryWrapper);

                                    if (existingEmployee != null) {
                                        employee.setId(existingEmployee.getId());
                                        employee.setDeleted(0);
                                        wecomEmployeeMapper.updateById(employee);
                                    } else {
                                        employee.setDeleted(0);
                                        wecomEmployeeMapper.insert(employee);
                                    }

                                    employeeUserids.add(dto.getUserid());
                                    successCount++;
                                } catch (Exception e) {
                                    log.error("保存员工失败: {}", dto.getName(), e);
                                    failCount++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("处理部门 {} 的员工失败", department.getName(), e);
                }
            }

            // 5. 删除不在本次同步中的员工（软删除）
            LambdaQueryWrapper<WecomEmployee> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(WecomEmployee::getWecomCorpId, wecomCorpId)
                    .eq(WecomEmployee::getDeleted, 0);
            List<WecomEmployee> allEmployees = wecomEmployeeMapper.selectList(deleteWrapper);
            for (WecomEmployee emp : allEmployees) {
                if (!employeeUserids.contains(emp.getUserid())) {
                    emp.setDeleted(1);
                    wecomEmployeeMapper.updateById(emp);
                }
            }

            // 6. 更新同步日志
            syncLog.setTotalCount(totalCount);
            syncLog.setSuccessCount(successCount);
            syncLog.setFailCount(failCount);
            syncLog.setSyncStatus(failCount > 0 ? "PARTIAL" : "SUCCESS");
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);

            return String.format("员工同步完成，总数: %d，成功: %d，失败: %d", totalCount, successCount, failCount);
        } catch (Exception e) {
            log.error("同步员工失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步员工失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncEmployeeDetails(Long wecomCorpId) throws CommonException {
        WecomSyncLog syncLog = createSyncLog(wecomCorpId, "EMPLOYEE_DETAIL");

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

            // 4. 遍历员工获取详细信息
            int totalCount = employees.size();
            int successCount = 0;
            int failCount = 0;

            for (WecomEmployee employee : employees) {
                try {
                    WecomEmployeeDetailResponse response = wecomApiService.getEmployeeDetail(
                            wecomCorp, accessToken, employee.getUserid());

                    if (response.getErrcode() != null && response.getErrcode() != 0) {
                        log.warn("获取员工 {} 的详情失败: {}", employee.getName(), response.getErrmsg());
                        failCount++;
                        continue;
                    }

                    WecomEmployeeDetail detail = convertToEmployeeDetail(response, employee.getId());
                    detail.setUserid(employee.getUserid());

                    // 查找是否已存在
                    LambdaQueryWrapper<WecomEmployeeDetail> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(WecomEmployeeDetail::getEmployeeId, employee.getId());
                    WecomEmployeeDetail existingDetail = wecomEmployeeDetailMapper.selectOne(queryWrapper);

                    if (existingDetail != null) {
                        detail.setId(existingDetail.getId());
                        detail.setDeleted(0);
                        wecomEmployeeDetailMapper.updateById(detail);
                    } else {
                        detail.setDeleted(0);
                        wecomEmployeeDetailMapper.insert(detail);
                    }

                    successCount++;
                } catch (Exception e) {
                    log.error("保存员工详情失败: {}", employee.getName(), e);
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

            return String.format("员工详情同步完成，总数: %d，成功: %d，失败: %d", totalCount, successCount, failCount);
        } catch (Exception e) {
            log.error("同步员工详情失败", e);
            syncLog.setSyncStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setEndTime(LocalDateTime.now());
            wecomSyncLogMapper.updateById(syncLog);
            throw new CommonException("同步员工详情失败: " + e.getMessage());
        }
    }

    @Override
    public String syncAll(Long wecomCorpId) throws CommonException {
        StringBuilder result = new StringBuilder();

        result.append("开始全量同步...\n");

        // 1. 同步部门
        result.append(syncDepartments(wecomCorpId)).append("\n");

        // 2. 同步员工
        result.append(syncEmployees(wecomCorpId)).append("\n");

        // 3. 同步员工详情
        result.append(syncEmployeeDetails(wecomCorpId)).append("\n");

        result.append("全量同步完成");

        return result.toString();
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

    /**
     * 转换部门DTO为实体
     */
    private WecomDepartment convertToDepartment(WecomDepartmentDto dto, Long wecomCorpId) {
        WecomDepartment department = new WecomDepartment();
        department.setWecomCorpId(wecomCorpId);
        department.setDepartmentId(dto.getId());
        department.setName(dto.getName());
        department.setNameEn(dto.getNameEn());
        department.setParentId(dto.getParentId());
        department.setOrder(dto.getOrder() != null ? dto.getOrder().intValue() : null);
        department.setDepartmentLeader(dto.getDepartmentLeader() != null ?
                String.join(",", dto.getDepartmentLeader()) : null);
        department.setUpdateTime(LocalDateTime.now());
        return department;
    }

    /**
     * 转换员工DTO为实体
     */
    private WecomEmployee convertToEmployee(WecomEmployeeDto dto, Long wecomCorpId) {
        WecomEmployee employee = new WecomEmployee();
        employee.setWecomCorpId(wecomCorpId);
        employee.setUserid(dto.getUserid());
        employee.setName(dto.getName());
        employee.setAlias(dto.getAlias());
        employee.setMobile(dto.getMobile());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment() != null ?
                dto.getDepartment().stream().map(String::valueOf).collect(Collectors.joining(",")) : null);
        employee.setOrderInDept(dto.getOrderInDept() != null ?
                dto.getOrderInDept().stream().map(String::valueOf).collect(Collectors.joining(",")) : null);
        employee.setPosition(dto.getPosition());
        employee.setGender(dto.getGender());
        employee.setAvatar(dto.getAvatar());
        employee.setTelephone(dto.getTelephone());
        employee.setEnable(dto.getEnable());
        employee.setIsLeader(dto.getIsLeader());
        employee.setStatus(dto.getStatus());
        employee.setQrCode(dto.getQrCode());
        employee.setExternalPosition(dto.getExternalPosition());

        // 处理扩展属性
        if (dto.getExtattr() != null) {
            try {
                employee.setExtattr(objectMapper.writeValueAsString(dto.getExtattr()));
            } catch (Exception e) {
                log.warn("序列化extattr失败", e);
            }
        }

        // 处理对外属性
        if (dto.getExternalProfile() != null) {
            try {
                employee.setExternalProfile(objectMapper.writeValueAsString(dto.getExternalProfile()));
            } catch (Exception e) {
                log.warn("序列化external_profile失败", e);
            }
        }

        employee.setUpdateTime(LocalDateTime.now());
        return employee;
    }

    /**
     * 转换员工详情DTO为实体
     */
    private WecomEmployeeDetail convertToEmployeeDetail(WecomEmployeeDetailResponse dto, Long employeeId) {
        WecomEmployeeDetail detail = new WecomEmployeeDetail();
        detail.setEmployeeId(employeeId);
        detail.setEnglishName(dto.getEnglishName());
        detail.setAddress(dto.getAddress());
        detail.setDirectLeader(dto.getDirectLeader() != null ?
                String.join(",", dto.getDirectLeader()) : null);
        detail.setMainDepartment(dto.getMainDepartment() != null ? String.valueOf(dto.getMainDepartment()) : null);
        detail.setHide(dto.getHide());
        detail.setSenior(dto.getSenior());
        detail.setJoinTime(dto.getJointime());
        detail.setFirstParty(dto.getFirstParty());
        detail.setUpdateTime(LocalDateTime.now());
        return detail;
    }
}