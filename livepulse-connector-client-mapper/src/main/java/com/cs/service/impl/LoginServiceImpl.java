package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.constant.CommonConstants;
import com.cs.entity.SysUser;
import com.cs.exception.CommonException;
import com.cs.param.*;
import com.cs.resp.PageResult;
import com.cs.service.LoginService;
import com.cs.service.SysUserService;
import com.cs.util.UserContext;
import com.cs.vo.SysUserVO;
import com.cs.vo.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: LoginServiceImpl
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/4 17:12
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${login.expire.time}")
    private String loginExpireTime;


    private final static PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserLoginVO userLogin(UserLoginParam userLoginParam) throws CommonException, JsonProcessingException {
        checkLogin(userLoginParam);
        // 根据用户名查询用户信息 如果用户名是手机号 则查询手机号
        String userName = userLoginParam.getUserName();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (userName.matches("^1[3-9]\\d{8}$")) {
            queryWrapper.eq(SysUser::getPhone, userName);
        } else {
            queryWrapper.eq(SysUser::getUserName, userName);
        }
        SysUser user = sysUserService.getOne(queryWrapper);
        if (Objects.isNull(user)) {
            throw new CommonException("The user does not exist.");
        }
        String password = userLoginParam.getPassword();
        if (!encoder.matches(password, user.getPassword())) {
            throw new CommonException("The password is incorrect.");
        }
        UserLoginVO userLoginVO = mapperFactory.getMapperFacade().map(user, UserLoginVO.class);
        String userInfo = objectMapper.writeValueAsString(userLoginVO);
        Long userId = user.getUserId();
        String token = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
        userLoginVO.setToken(token);
        userLoginVO.setExpire(Integer.parseInt(loginExpireTime)*3600);
        stringRedisTemplate.opsForValue().set(CommonConstants.TOKEN + token, userInfo, Long.parseLong(loginExpireTime), TimeUnit.HOURS);
        return userLoginVO;
    }

    private void checkLogin(UserLoginParam userLoginParam) throws CommonException {
        // 0. 校验参数
        if (Objects.isNull(userLoginParam)) {
            throw new CommonException("The parameter cannot be empty.");
        }
        // 1. 校验用户名密码
        if (Objects.isNull(userLoginParam.getUserName()) || Objects.isNull(userLoginParam.getPassword())) {
            throw new CommonException("The username or password cannot be empty.");
        }
    }

    @Override
    public PageResult<SysUserVO> userPageList(SysUserPageParam sysUserPageParam) throws CommonException {
        // 校验参数
        if (Objects.isNull(sysUserPageParam)) {
            throw new CommonException("The parameter cannot be empty.");
        }
        PageQueryParam pageQueryParam = sysUserPageParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            pageQueryParam = new PageQueryParam();
        }
        pageQueryParam.setPageNo(pageQueryParam.getPageNo() == null ? 1 : pageQueryParam.getPageNo());
        pageQueryParam.setPageSize(pageQueryParam.getPageSize() == null ? 10 : pageQueryParam.getPageSize());
        Page<SysUser> page = new Page<>(pageQueryParam.getPageNo(), pageQueryParam.getPageSize());

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(SysUser::getUserId, 0);
        if (StringUtils.isNotBlank(sysUserPageParam.getUserName()))  {
            queryWrapper.like(SysUser::getUserName, sysUserPageParam.getUserName());
        } else if (Objects.nonNull(sysUserPageParam.getType())) {
            queryWrapper.eq(SysUser::getType, sysUserPageParam.getType());
        }
        queryWrapper.orderByDesc(SysUser::getUpdateTime);

        IPage<SysUser> sysUserPage = sysUserService.page(page, queryWrapper);
        PageResult<SysUserVO> pageResult = mapperFactory.getMapperFacade().map(sysUserPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(sysUserPage.getRecords(), SysUserVO.class));
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean editPassword(UpdateUserPasswordParam updateUserPasswordParam) throws CommonException {
        SysUserVO userInfo = UserContext.getCurrentUser();
        if (Objects.isNull(userInfo)) {
            throw new CommonException("userInfo is null");
        }
        if (Objects.nonNull(userInfo.getUserId()))  {
            throw new CommonException("The userId is null");
        }
        // 校验参数
        if (Objects.isNull(updateUserPasswordParam)) {
            throw new CommonException("The parameter cannot be empty.");
        }
        if (Objects.isNull(updateUserPasswordParam.getUserId()) || Objects.isNull(updateUserPasswordParam.getPassword()) || Objects.isNull(updateUserPasswordParam.getConfirmPassword())) {
            throw new CommonException("The userId or password or confirmPassword cannot be empty.");
        }
        if (!updateUserPasswordParam.getPassword().equals(updateUserPasswordParam.getConfirmPassword())) {
            throw new CommonException("The password and confirmPassword are inconsistent.");
        }
        SysUser sysUser = sysUserService.getById(userInfo.getUserId());
        if (Objects.isNull(sysUser)) {
            throw new CommonException("The user does not exist.");
        }
        String password = encoder.encode(updateUserPasswordParam.getPassword());
        sysUser.setPassword(password);
        sysUser.setUpdateTime(LocalDateTime.now());
        if (!sysUserService.updateById(sysUser))  {
            throw new CommonException("Update failed.");
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean updateUserInfo(SysUserParam sysUserParam) throws CommonException {
        SysUserVO userInfo = UserContext.getCurrentUser();
        if (Objects.isNull(userInfo)) {
            throw new CommonException("userInfo is null");
        }
        if (Objects.nonNull(userInfo.getUserId()))  {
            throw new CommonException("The userId is null");
        }
        if (Objects.isNull(sysUserParam)) {
            throw new CommonException("The parameter cannot be empty.");
        }
        SysUser sysUser = sysUserService.getById(userInfo.getUserId());
        if (Objects.isNull(sysUser)) {
            throw new CommonException("The user does not exist.");
        }
        SysUser updateSysUser = new SysUser();
        updateSysUser.setUserId(userInfo.getUserId());
        if (StringUtils.isNotBlank(sysUserParam.getNickName()))  {
            updateSysUser.setNickName(sysUserParam.getNickName());
        }
        if (StringUtils.isNotBlank(sysUserParam.getEmail()))  {
            updateSysUser.setEmail(sysUserParam.getEmail());
        }
        updateSysUser.setUpdateTime(LocalDateTime.now());
        if (!sysUserService.saveOrUpdate(updateSysUser))  {
            throw new CommonException("Update failed.");
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean avatar(MultipartFile file) {
        return null;
    }

    @Override
    public SysUserVO userInfo() throws CommonException {
        SysUserVO userInfo = UserContext.getCurrentUser();
        if (Objects.isNull(userInfo)) {
            throw new CommonException("userInfo is null");
        }
        if (Objects.isNull(userInfo.getUserId()))  {
            throw new CommonException("The userId is null");
        }
        SysUser sysUser = sysUserService.getById(userInfo.getUserId());
        if (Objects.isNull(sysUser)) {
            throw new CommonException("The user does not exist.");
        }
        return mapperFactory.getMapperFacade().map(sysUser, SysUserVO.class);
    }
}
