package com.cs.controller;

import com.cs.exception.CommonException;
import com.cs.param.MetaTablePageParam;
import com.cs.param.MetaTableParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.MetaTableColumnService;
import com.cs.service.MetaTableService;
import com.cs.vo.MetaTableColumnVO;
import com.cs.vo.MetaTableVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: MetaTableController
 * @Author: wwd
 * @TODO: MetaTable 控制器
 * @Date: 2026/3/25
 */
@RestController
@RequestMapping("/metaTable")
@Tag(name = "MetaTable管理")
public class MetaTableController {

    @Autowired
    private MetaTableService metaTableService;

    @Autowired
    private MetaTableColumnService metaTableColumnService;

    @PostMapping("/pageList")
    @Operation(summary = "分页查询列表")
    public RespResult<PageResult<MetaTableVO>> pageList(@RequestBody MetaTablePageParam pageParam) throws CommonException {
        return RespResult.success(metaTableService.pageList(pageParam));
    }

    @GetMapping("/detail/{tableId}")
    @Operation(summary = "根据ID查询详情")
    public RespResult<MetaTableVO> detail(@PathVariable("tableId") Long tableId) throws CommonException {
        return RespResult.success(metaTableService.getDetailById(tableId));
    }

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "保存或更新")
    public RespResult<Boolean> saveOrUpdate(@RequestBody MetaTableParam param) throws CommonException {
        return RespResult.success(metaTableService.saveOrUpdate(param));
    }

    @DeleteMapping("/delete/{tableId}")
    @Operation(summary = "删除")
    public RespResult<Boolean> delete(@PathVariable("tableId") Long tableId) throws CommonException {
        return RespResult.success(metaTableService.deleteById(tableId));
    }

    @GetMapping("/columns/{tableId}")
    @Operation(summary = "根据表ID查询对应的列信息")
    public RespResult<List<MetaTableColumnVO>> getColumnsByTableId(@PathVariable("tableId") Long tableId) throws CommonException {
        return RespResult.success(metaTableColumnService.listByTableId(tableId));
    }
}