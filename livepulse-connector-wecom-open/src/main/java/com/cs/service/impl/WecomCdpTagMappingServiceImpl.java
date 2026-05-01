package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.entity.WecomCdpTagMapping;
import com.cs.entity.WecomCorp;
import com.cs.entity.WecomCorpTags;
import com.cs.entity.WecomEmployee;
import com.cs.entity.WecomExternalTags;
import com.cs.entity.WecomMaster;
import com.cs.entity.WecomTagGroups;
import com.cs.exception.CommonException;
import com.cs.mapper.WecomCdpTagMappingMapper;
import com.cs.mapper.WecomCorpTagsMapper;
import com.cs.mapper.WecomEmployeeMapper;
import com.cs.mapper.WecomExternalTagsMapper;
import com.cs.mapper.WecomMasterMapper;
import com.cs.mapper.WecomTagGroupsMapper;
import com.cs.param.WecomCdpTagMappingPageParam;
import com.cs.param.WecomCdpTagMappingParam;
import com.cs.resp.PageResult;
import com.cs.service.WecomApiService;
import com.cs.service.WecomCdpTagMappingService;
import com.cs.service.WecomCorpService;
import com.cs.vo.WecomCdpTagMappingVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * CDP标签映射 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-03-21
 */
@Slf4j
@Service
public class WecomCdpTagMappingServiceImpl extends ServiceImpl<WecomCdpTagMappingMapper, WecomCdpTagMapping> implements WecomCdpTagMappingService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private WecomTagGroupsMapper wecomTagGroupsMapper;

    @Autowired
    private WecomCorpTagsMapper wecomCorpTagsMapper;

    @Autowired
    private WecomMasterMapper wecomMasterMapper;

    @Autowired
    private WecomEmployeeMapper wecomEmployeeMapper;

    @Autowired
    private WecomExternalTagsMapper wecomExternalTagsMapper;

    @Autowired
    private WecomApiService wecomApiService;

    @Autowired
    private WecomCorpService wecomCorpService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResult<WecomCdpTagMappingVO> pageList(WecomCdpTagMappingPageParam pageParam) throws CommonException {
        if (Objects.isNull(pageParam)) {
            throw new CommonException("param is null");
        }
        Integer current = pageParam.getCurrentPage();
        Integer size = pageParam.getPageSize();
        if (Objects.isNull(current)) {
            current = 1;
        }
        if (Objects.isNull(size)) {
            size = 10;
        }

        LambdaQueryWrapper<WecomCdpTagMapping> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(pageParam.getWecomCorpId())) {
            queryWrapper.eq(WecomCdpTagMapping::getWecomCorpId, pageParam.getWecomCorpId());
        }
        if (StringUtils.isNotBlank(pageParam.getCdpTagName())) {
            queryWrapper.like(WecomCdpTagMapping::getCdpTagName, pageParam.getCdpTagName());
        }
        if (Objects.nonNull(pageParam.getSyncStatus())) {
            queryWrapper.eq(WecomCdpTagMapping::getSyncStatus, pageParam.getSyncStatus());
        }
        queryWrapper.eq(WecomCdpTagMapping::getDeleted, 0);
        queryWrapper.orderByDesc(WecomCdpTagMapping::getCreateTime);

        Page<WecomCdpTagMapping> page = new Page<>(current, size);
        IPage<WecomCdpTagMapping> iPage = page(page, queryWrapper);
        PageResult<WecomCdpTagMappingVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), WecomCdpTagMappingVO.class));

        return pageResult;
    }

    @Override
    public WecomCdpTagMappingVO getByIdVO(Long id) throws CommonException {
        if (Objects.isNull(id)) {
            throw new CommonException("id is null");
        }
        WecomCdpTagMapping mapping = getById(id);
        if (Objects.isNull(mapping) || mapping.getDeleted() == 1) {
            throw new CommonException("CDP标签映射不存在");
        }
        return mapperFactory.getMapperFacade().map(mapping, WecomCdpTagMappingVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(WecomCdpTagMappingParam param) throws CommonException {
        if (Objects.isNull(param)) {
            throw new CommonException("param is null");
        }
        if (Objects.isNull(param.getWecomCorpId())) {
            throw new CommonException("企业微信账户ID不能为空");
        }
        if (StringUtils.isBlank(param.getCdpTagName())) {
            throw new CommonException("CDP标签名称不能为空");
        }
        if (StringUtils.isBlank(param.getWecomTagGroupId()) || StringUtils.isBlank(param.getWecomTagName())) {
            throw new CommonException("企业微信标签组和标签名称不能为空");
        }

        // 验证企业微信标签组是否存在
        LambdaQueryWrapper<WecomTagGroups> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(WecomTagGroups::getGroupId, param.getWecomTagGroupId())
                .eq(WecomTagGroups::getWecomCorpId, param.getWecomCorpId())
                .eq(WecomTagGroups::getDeleted, 0);
        WecomTagGroups tagGroup = wecomTagGroupsMapper.selectOne(groupWrapper);
        if (Objects.isNull(tagGroup)) {
            throw new CommonException("企业微信标签组不存在");
        }

        // 验证企业微信标签是否存在
        LambdaQueryWrapper<WecomCorpTags> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(WecomCorpTags::getTagId, param.getWecomTagId())
                .eq(WecomCorpTags::getWecomCorpId, param.getWecomCorpId())
                .eq(WecomCorpTags::getDeleted, 0);
        WecomCorpTags wecomTag = wecomCorpTagsMapper.selectOne(tagWrapper);
        if (Objects.isNull(wecomTag)) {
            throw new CommonException("企业微信标签不存在");
        }

        WecomCdpTagMapping mapping = mapperFactory.getMapperFacade().map(param, WecomCdpTagMapping.class);
        mapping.setWecomTagGroupName(tagGroup.getGroupName());
        mapping.setSyncStatus(0); // 设置为未同步状态

        if (Objects.isNull(mapping.getId())) {
            mapping.setCreateTime(LocalDateTime.now());
            return save(mapping);
        } else {
            mapping.setUpdateTime(LocalDateTime.now());
            return updateById(mapping);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) throws CommonException {
        if (Objects.isNull(id)) {
            throw new CommonException("id is null");
        }
        WecomCdpTagMapping mapping = getById(id);
        if (Objects.isNull(mapping)) {
            throw new CommonException("CDP标签映射不存在");
        }
        mapping.setDeleted(1);
        mapping.setUpdateTime(LocalDateTime.now());
        return updateById(mapping);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncCdpTagsToWecom(Long id) throws CommonException {
        if (Objects.isNull(id)) {
            throw new CommonException("id is null");
        }
        WecomCdpTagMapping mapping = getById(id);
        if (Objects.isNull(mapping)) {
            throw new CommonException("CDP标签映射不存在");
        }
        Long wecomCorpId = mapping.getWecomCorpId();

        try {
            // 获取企业微信账户信息
            WecomCorp wecomCorp = wecomCorpService.getById(wecomCorpId);
            if (Objects.isNull(wecomCorp)) {
                throw new CommonException("企业微信账户不存在");
            }

            // 获取 access_token
            String accessToken = wecomApiService.getAccessToken(wecomCorp);
            if (StringUtils.isBlank(accessToken)) {
                throw new CommonException("获取 access_token 失败");
            }

            // 1. 从 wecom_employees 表中随机选择一个不为空的 userid
            LambdaQueryWrapper<WecomEmployee> employeeWrapper = new LambdaQueryWrapper<>();
            employeeWrapper.isNotNull(WecomEmployee::getUserid)
                    .eq(WecomEmployee::getWecomCorpId, wecomCorpId)
                    .eq(WecomEmployee::getDeleted, 0);
            List<WecomEmployee> employees = wecomEmployeeMapper.selectList(employeeWrapper);

            if (employees == null || employees.isEmpty()) {
                throw new CommonException("没有找到可用的员工信息");
            }

            // 随机选择一个员工
            Random random = new Random();
            WecomEmployee selectedEmployee = employees.get(random.nextInt(employees.size()));
            String userid = selectedEmployee.getUserid();

            log.info("随机选择的员工userid: {}", userid);

            // 2. 从 wecom_external_tags 表中获取所有标签ID列表
            LambdaQueryWrapper<WecomExternalTags> tagsWrapper = new LambdaQueryWrapper<>();
            tagsWrapper.select(WecomExternalTags::getTagId)
                    .isNotNull(WecomExternalTags::getTagId)
                    .eq(WecomExternalTags::getDeleted, 0);
            List<WecomExternalTags> externalTags = wecomExternalTagsMapper.selectList(tagsWrapper);

            if (externalTags == null || externalTags.isEmpty()) {
                return "没有找到可用的外部联系人标签";
            }

            // 去重并构建 add_tag 列表
            List<String> addTagList = new ArrayList<>();
            for (WecomExternalTags tag : externalTags) {
                if (StringUtils.isNotBlank(tag.getTagId()) && !addTagList.contains(tag.getTagId())) {
                    addTagList.add(tag.getTagId());
                }
            }

            if (addTagList.isEmpty()) {
                return "没有有效的标签ID可以同步";
            }

            log.info("准备同步的标签列表: {}", addTagList);

            // 3. 从 wecom_master 表中获取所有 external_userid
            LambdaQueryWrapper<WecomMaster> masterWrapper = new LambdaQueryWrapper<>();
            masterWrapper.select(WecomMaster::getExternalUserid)
                    .isNotNull(WecomMaster::getExternalUserid)
                    .eq(WecomMaster::getExternalUserid, "")
                    .ne(WecomMaster::getExternalUserid, "");
            List<WecomMaster> allMasters = wecomMasterMapper.selectList(masterWrapper);

            if (allMasters == null || allMasters.isEmpty()) {
                return "没有找到可用的外部联系人";
            }

            int totalCount = allMasters.size();
            int successCount = 0;
            int failCount = 0;

            // 4. 按1000条分页处理
            int pageSize = 1000;
            int totalPages = (totalCount + pageSize - 1) / pageSize;

            log.info("开始同步CDP标签到企业微信，总记录数: {}, 分 {} 批处理", totalCount, totalPages);

            for (int page = 0; page < totalPages; page++) {
                int fromIndex = page * pageSize;
                int toIndex = Math.min(fromIndex + pageSize, totalCount);
                List<WecomMaster> pageMasters = allMasters.subList(fromIndex, toIndex);

                log.info("处理第 {}/{} 批，记录数: {}", page + 1, totalPages, pageMasters.size());

                for (WecomMaster master : pageMasters) {
                    try {
                        String externalUserid = master.getExternalUserid();

                        // 调用企业微信接口编辑客户企业标签
                        boolean result = markTagToWecom(accessToken, userid, externalUserid, addTagList);

                        if (result) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                    } catch (Exception e) {
                        log.error("标记标签失败，external_userid: {}, error: {}", master.getExternalUserid(), e.getMessage());
                        failCount++;
                    }
                }

                // 每批之间暂停100ms，避免请求过快
                if (page < totalPages - 1) {
                    Thread.sleep(100);
                }
            }

            String resultMsg = String.format("同步完成，总数: %d，成功: %d，失败: %d", totalCount, successCount, failCount);
            log.info(resultMsg);
            return resultMsg;

        } catch (Exception e) {
            log.error("同步CDP标签到企业微信失败", e);
            throw new CommonException("同步失败: " + e.getMessage());
        }
    }

    /**
     * 调用企业微信接口标记客户企业标签
     *
     * @param accessToken    访问令牌
     * @param userid         成员userid
     * @param externalUserid 外部联系人userid
     * @param addTagList     要添加的标签列表
     * @return 是否成功
     */
    private boolean markTagToWecom(String accessToken, String userid, String externalUserid, List<String> addTagList) throws CommonException {
        try {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/mark_tag?access_token=" + accessToken;

            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("userid", userid);
            requestBody.put("external_userid", externalUserid);

            // 添加标签列表（企业微信要求字符串数组）
            ArrayNode addTagNode = requestBody.putArray("add_tag");
            for (String tagId : addTagList) {
                addTagNode.add(tagId);
            }

            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("请求企业微信标记标签接口，请求体: {}", requestBodyStr);

            // 调用接口
            String response = wecomApiService.doPost(url, requestBodyStr);
            log.info("企业微信接口响应: {}", response);

            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(response);
            int errcode = jsonNode.path("errcode").asInt();

            if (errcode == 0) {
                log.info("标记标签成功，userid: {}, external_userid: {}", userid, externalUserid);
                return true;
            } else {
                String errmsg = jsonNode.path("errmsg").asText();
                log.warn("标记标签失败，errcode: {}, errmsg: {}", errcode, errmsg);
                return false;
            }
        } catch (Exception e) {
            log.error("调用企业微信标记标签接口异常", e);
            throw new CommonException("调用企业微信接口失败: " + e.getMessage());
        }
    }
}