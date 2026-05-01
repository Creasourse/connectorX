package com.cs.service;

import com.cs.BaseTest;
import com.cs.entity.MetaTable;
import com.cs.exception.CommonException;
import com.cs.param.MetaTablePageParam;
import com.cs.param.MetaTableParam;
import com.cs.resp.PageResult;
import com.cs.vo.MetaTableVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: MetaTableServiceTest
 * @Author: wwd
 * @TODO: MetaTable Service 测试
 * @Date: 2026/3/27
 */
@DisplayName("MetaTable Service 测试")
public class MetaTableServiceTest extends BaseTest {

    @Autowired
    private MetaTableService metaTableService;

    private MetaTableParam testParam;

    @BeforeEach
    public void setUp() {
        testParam = new MetaTableParam();
        testParam.setTableName("test_table");
        testParam.setTableAlias("测试表");
        testParam.setComment("测试表描述");
    }

    @Test
    @DisplayName("测试保存MetaTable - 成功")
    public void testSave_Success() throws CommonException {
        boolean result = metaTableService.saveOrUpdate(testParam);
        Assertions.assertTrue(result, "保存应该成功");
    }

    @Test
    @DisplayName("测试保存MetaTable - 表名为空抛异常")
    public void testSave_NullTableName() {
        testParam.setTableName(null);
        Assertions.assertThrows(CommonException.class, () -> {
            metaTableService.saveOrUpdate(testParam);
        });
    }

    @Test
    @DisplayName("测试分页查询MetaTable - 成功")
    public void testPageList_Success() throws CommonException {
        MetaTablePageParam pageParam = new MetaTablePageParam();
        pageParam.setCurrentPage(1);
        pageParam.setPageSize(10);

        PageResult<MetaTableVO> result = metaTableService.pageList(pageParam);

        Assertions.assertNotNull(result, "返回结果不应为空");
        Assertions.assertNotNull(result.getList(), "列表不应为空");
    }

    @Test
    @DisplayName("测试根据ID查询MetaTable详情 - ID为空抛异常")
    public void testGetDetailById_Null() {
        Assertions.assertThrows(CommonException.class, () -> {
            metaTableService.getDetailById(null);
        });
    }

    @Test
    @DisplayName("测试删除MetaTable - ID为空抛异常")
    public void testDeleteById_Null() {
        Assertions.assertThrows(CommonException.class, () -> {
            metaTableService.deleteById(null);
        });
    }
}
