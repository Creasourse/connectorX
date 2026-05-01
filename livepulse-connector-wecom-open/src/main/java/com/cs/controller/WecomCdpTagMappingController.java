package com.cs.controller;

import com.cs.param.WecomCdpTagMappingPageParam;
import com.cs.param.WecomCdpTagMappingParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.WecomCdpTagMappingService;
import com.cs.vo.WecomCdpTagMappingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: WecomCdpTagMappingController
 * @Author: wwd
 * @TODO: CDP标签映射管理控制器
 * @Date: 2026/3/21
 */
@Slf4j
@RestController
@RequestMapping("/wecomCdpTag")
@Tag(name = "CDP标签管理")
public class WecomCdpTagMappingController {

    @Autowired
    private WecomCdpTagMappingService wecomCdpTagMappingService;

    @PostMapping("/pageList")
    @Operation(summary = "分页查询CDP标签映射")
    public RespResult<PageResult<WecomCdpTagMappingVO>> pageList(@RequestBody WecomCdpTagMappingPageParam pageParam) {
        try {
            return RespResult.success(wecomCdpTagMappingService.pageList(pageParam));
        } catch (Exception e) {
            log.error("分页查询CDP标签映射失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "根据ID查询CDP标签映射详情")
    public RespResult<WecomCdpTagMappingVO> detail(@PathVariable("id") Long id) {
        try {
            return RespResult.success(wecomCdpTagMappingService.getByIdVO(id));
        } catch (Exception e) {
            log.error("查询CDP标签映射详情失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "保存或更新CDP标签映射")
    public RespResult<Boolean> saveOrUpdate(@RequestBody WecomCdpTagMappingParam param) {
        try {
            return RespResult.success(wecomCdpTagMappingService.saveOrUpdate(param));
        } catch (Exception e) {
            log.error("保存或更新CDP标签映射失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除CDP标签映射")
    public RespResult<Boolean> delete(@PathVariable("id") Long id) {
        try {
            return RespResult.success(wecomCdpTagMappingService.deleteById(id));
        } catch (Exception e) {
            log.error("删除CDP标签映射失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    @PostMapping("/sync/{id}")
    @Operation(summary = "同步CDP标签到企业微信")
    public RespResult<String> syncCdpTagsToWecom(@PathVariable("id") Long id) {
        try {
            return RespResult.success(wecomCdpTagMappingService.syncCdpTagsToWecom(id));
        } catch (Exception e) {
            log.error("同步CDP标签失败", e);
            return RespResult.failure(e.getMessage());
        }
    }
}