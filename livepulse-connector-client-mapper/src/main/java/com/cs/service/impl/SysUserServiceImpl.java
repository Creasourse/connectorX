package com.cs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.constant.CommonConstants;
import com.cs.entity.SysUser;
import com.cs.exception.CommonException;
import com.cs.mapper.SysUserMapper;
import com.cs.param.ForgetPasswordParam;
import com.cs.param.SysUserParam;
import com.cs.service.SysUserService;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wwd
 * @since 2026-01-30
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MapperFactory mapperFactory;

    private final static PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Boolean register(SysUserParam sysUserParam) throws CommonException {
        checkRegister(sysUserParam);
        // 检查用户名是否已存在
        SysUser existUser = this.lambdaQuery()
                .eq(SysUser::getUserName, sysUserParam.getUserName())
                .one();
        if (Objects.nonNull(existUser)) {
            throw new CommonException("用户名已存在");
        }

        // 检查手机号是否已存在
        existUser = this.lambdaQuery()
                .eq(SysUser::getPhone, sysUserParam.getPhone())
                .one();
        if (Objects.nonNull(existUser)) {
            throw new CommonException("手机号已被注册");
        }

        // 检查邮箱是否已存在
        existUser = this.lambdaQuery()
                .eq(SysUser::getEmail, sysUserParam.getEmail())
                .one();
        if (Objects.nonNull(existUser)) {
            throw new CommonException("邮箱已被注册");
        }
        SysUser sysUser = mapperFactory.getMapperFacade().map(sysUserParam, SysUser.class);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUser.setType(Short.valueOf("2"));
        String password = sysUser.getPassword();
        password = encoder.encode(password);
        sysUser.setPassword(password);
        if (!saveOrUpdate(sysUser)) {
            throw new CommonException("保存用户失败");
        }
        return Boolean.TRUE;
    }

    private void checkRegister(SysUserParam sysUserParam) throws CommonException {
        if (Objects.isNull(sysUserParam)) {
            throw new CommonException("param is null");
        }
        if (Objects.isNull(sysUserParam.getUserName())) {
            throw new CommonException("userName param is null");
        }
        if (sysUserParam.getUserName().length() > 50) {
            throw new CommonException("The username exceeds the maximum length. ");
        }
        if (Objects.isNull(sysUserParam.getPassword())) {
            throw new CommonException("password param is null");
        }
        if (sysUserParam.getPassword().length() > 50) {
            throw new CommonException("密码长度不能超过 50 个字符");
        }
        if (sysUserParam.getPassword().length() < 6) {
            throw new CommonException("密码长度不能小于 6 个字符");
        }
        if (Objects.isNull(sysUserParam.getEmail())) {
            throw new CommonException("email param is null");
        }
        if (Objects.isNull(sysUserParam.getPhone())) {
            throw new CommonException("phone param is null");
        }

    }

    @Override
    public Boolean forgetPassword(ForgetPasswordParam forgetPasswordParam) throws CommonException {
        // 参数验证
        if (Objects.isNull(forgetPasswordParam)) {
            throw new CommonException("param is null");
        }
        if (Objects.isNull(forgetPasswordParam.getPhone())) {
            throw new CommonException("phone param is null");
        }
        if (Objects.isNull(forgetPasswordParam.getPassword())) {
            throw new CommonException("password param is null");
        }
        if (forgetPasswordParam.getPassword().length() < 6) {
            throw new CommonException("密码长度不能小于 6 个字符");
        }
        if (forgetPasswordParam.getPassword().length() > 50) {
            throw new CommonException("密码长度不能超过 50 个字符");
        }
        if (Objects.isNull(forgetPasswordParam.getConfirmPassword())) {
            throw new CommonException("confirmPassword param is null");
        }
        // 验证两次密码输入是否一致
        if (!forgetPasswordParam.getPassword().equals(forgetPasswordParam.getConfirmPassword())) {
            throw new CommonException("两次输入的密码不一致");
        }

        // 查询用户是否存在
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getPhone, forgetPasswordParam.getPhone())
                .one();
        if (Objects.isNull(user)) {
            throw new CommonException("该手机号未注册");
        }

        // 生成新盐值并加密密码
        String encryptedPassword = encoder.encode(forgetPasswordParam.getPassword());

        // 更新密码
        user.setPassword(encryptedPassword);
        boolean updateResult = user.updateById();
        if (!updateResult) {
            throw new CommonException("重置密码失败");
        }

        return Boolean.TRUE;
    }
}
