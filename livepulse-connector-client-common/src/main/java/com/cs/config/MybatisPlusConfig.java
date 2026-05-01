package com.cs.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置
 */
@EnableTransactionManagement
@Configuration
@EnableConfigurationProperties(MybatisPlusProperties.class)
@MapperScan(basePackages = "com.cs.mapper")
public class MybatisPlusConfig {

    /**
     * MyBatis Plus 拦截器配置
     * 3.4.0版本之后推荐使用 MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setDbType(DbType.POSTGRE_SQL); // 数据库类型
        paginationInterceptor.setOverflow(true); // 超过最大页数时是否处理
        paginationInterceptor.setMaxLimit(1000L); // 单页分页条数限制

        // 设置溢出策略
        // true: 溢出时继续查询第一页
        // false: 溢出时抛出异常
        paginationInterceptor.setOverflow(false);

        interceptor.addInnerInterceptor(paginationInterceptor);

        // 2. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 4. SQL性能规范插件（防止恶意的全表更新删除）
        interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());

        return interceptor;
    }}