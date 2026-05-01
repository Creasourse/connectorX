package com.cs.controller;

import com.cs.exception.CommonException;
import com.cs.param.WecomCallbackConfigPageParam;
import com.cs.param.WecomCallbackConfigParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.WecomCallbackConfigService;
import com.cs.vo.WecomCallbackConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: WecomCallbackConfigController
 * @Author: wwd
 * @TODO: 企业微信回调配置控制器
 * @Date: 2026/3/25
 */
@RestController
@RequestMapping("/wecomCallbackConfig")
@Tag(name = "企业微信回调配置管理")
public class WecomCallbackConfigController {

    @Autowired
    private WecomCallbackConfigService wecomCallbackConfigService;

    @PostMapping("/pageList")
    @Operation(summary = "分页查询列表")
    public RespResult<PageResult<WecomCallbackConfigVO>> pageList(@RequestBody WecomCallbackConfigPageParam pageParam) throws CommonException {
        return RespResult.success(wecomCallbackConfigService.pageList(pageParam));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "根据ID查询详情")
    public RespResult<WecomCallbackConfigVO> detail(@PathVariable("id") Long id) throws CommonException {
        return RespResult.success(wecomCallbackConfigService.getDetailById(id));
    }

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "保存或更新")
    public RespResult<Boolean> saveOrUpdate(@RequestBody WecomCallbackConfigParam param) throws CommonException {
        return RespResult.success(wecomCallbackConfigService.saveOrUpdate(param));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除")
    public RespResult<Boolean> delete(@PathVariable("id") Long id) throws CommonException {
        return RespResult.success(wecomCallbackConfigService.deleteById(id));
    }
}