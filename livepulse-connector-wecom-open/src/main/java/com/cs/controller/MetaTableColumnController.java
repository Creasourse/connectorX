package com.cs.controller;

import com.cs.exception.CommonException;
import com.cs.param.MetaTableColumnPageParam;
import com.cs.param.MetaTableColumnParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.MetaTableColumnService;
import com.cs.vo.MetaTableColumnVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: MetaTableColumnController
 * @Author: wwd
 * @TODO: MetaTableColumn 控制器
 * @Date: 2026/3/25
 */
@RestController
@RequestMapping("/metaTableColumn")
@Tag(name = "MetaTableColumn管理")
public class MetaTableColumnController {

    @Autowired
    private MetaTableColumnService metaTableColumnService;

    @PostMapping("/pageList")
    @Operation(summary = "分页查询列表")
    public RespResult<PageResult<MetaTableColumnVO>> pageList(@RequestBody MetaTableColumnPageParam pageParam) throws CommonException {
        return RespResult.success(metaTableColumnService.pageList(pageParam));
    }

    @GetMapping("/detail/{tableColumnId}")
    @Operation(summary = "根据ID查询详情")
    public RespResult<MetaTableColumnVO> detail(@PathVariable("tableColumnId") Long tableColumnId) throws CommonException {
        return RespResult.success(metaTableColumnService.getDetailById(tableColumnId));
    }

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "保存或更新")
    public RespResult<Boolean> saveOrUpdate(@RequestBody MetaTableColumnParam param) throws CommonException {
        return RespResult.success(metaTableColumnService.saveOrUpdate(param));
    }

    @DeleteMapping("/delete/{tableColumnId}")
    @Operation(summary = "删除")
    public RespResult<Boolean> delete(@PathVariable("tableColumnId") Long tableColumnId) throws CommonException {
        return RespResult.success(metaTableColumnService.deleteById(tableColumnId));
    }
}