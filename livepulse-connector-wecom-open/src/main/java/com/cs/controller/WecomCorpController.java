package com.cs.controller;

import com.cs.exception.CommonException;
import com.cs.param.WecomCorpPageParam;
import com.cs.param.WecomCorpParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.WecomCorpService;
import com.cs.vo.WecomCorpVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: WecomCorpController
 * @Author: wwd
 * @TODO: 企业微信账户信息控制器
 * @Date: 2026/3/21
 */
@RestController
@RequestMapping("/wecomCorp")
@Tag(name = "企业微信账户管理")
public class WecomCorpController {

    @Autowired
    private WecomCorpService wecomCorpService;

    @PostMapping("/pageList")
    @Operation(summary = "分页查询企业微信账户列表")
    public RespResult<PageResult<WecomCorpVO>> pageList(@RequestBody WecomCorpPageParam pageParam) throws CommonException {
        return RespResult.success(wecomCorpService.pageList(pageParam));
    }

    @GetMapping("/detail/{wecomeCorpId}")
    @Operation(summary = "根据ID查询企业微信账户详情")
    public RespResult<WecomCorpVO> detail(@PathVariable("wecomeCorpId") Long wecomeCorpId) throws CommonException {
        return RespResult.success(wecomCorpService.getDetailById(wecomeCorpId));
    }

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "保存或更新企业微信账户")
    public RespResult<Boolean> saveOrUpdate(@RequestBody WecomCorpParam param) throws CommonException {
        return RespResult.success(wecomCorpService.saveOrUpdate(param));
    }

    @DeleteMapping("/delete/{wecomeCorpId}")
    @Operation(summary = "删除企业微信账户")
    public RespResult<Boolean> delete(@PathVariable("wecomeCorpId") Long wecomeCorpId) throws CommonException {
        return RespResult.success(wecomCorpService.deleteById(wecomeCorpId));
    }
}