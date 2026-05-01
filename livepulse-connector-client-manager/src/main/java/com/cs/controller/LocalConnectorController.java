package com.cs.controller;

import com.cs.annotation.TokenSign;
import com.cs.exception.CommonException;
import com.cs.param.LocalConnectorPageParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.LocalConnectorService;
import com.cs.vo.LocalConnectorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LocalConnectorController
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/11 10:18
 */
@RestController
@RequestMapping("/local/connector")
@Tag(name = "本地连接器接口")
public class LocalConnectorController {

    @Autowired
    private LocalConnectorService localConnectorService;

    @PostMapping("/pageList")
    @Operation(summary = "本地连接器分页列表")
    @TokenSign
    public RespResult<PageResult<LocalConnectorVO>> pageList(@RequestBody LocalConnectorPageParam localConnectorPageParam) throws CommonException {
        return RespResult.success(localConnectorService.pageList(localConnectorPageParam));
    }

    @PostMapping("/syncConnector")
    @Operation(summary = "同步购买插件")
    @TokenSign
    public RespResult<Boolean> syncConnector() throws CommonException {
        return RespResult.success(localConnectorService.syncConnector());
    }

}
