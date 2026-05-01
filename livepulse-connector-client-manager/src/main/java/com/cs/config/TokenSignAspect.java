package com.cs.config;

import com.cs.annotation.TokenSign;
import com.cs.constant.CommonConstants;
import com.cs.exception.CommonException;
import com.cs.util.UserContext;
import com.cs.vo.SysUserVO;
import com.cs.vo.UserLoginVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * @ClassName: TokenSignAspect
 * @Author: wwd
 * @TODO:
 * @Date: 2026/3/6 17:14
 */
@Aspect
@Component
@Slf4j
public class TokenSignAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MapperFactory mapperFactory;

    /**
     * 切入点：所有 Controller 方法
     */
    @Pointcut("@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerPointcut() {
    }

    /**
     * 定义切入点：所有被 @TokenSign 注解的方法
     */
    @Pointcut("@annotation(com.cs.annotation.TokenSign)")
    public void tokenSignPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("tokenSignPointcut() && controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new CommonException("无法获取请求上下文");
        }

        HttpServletRequest request = attributes.getRequest();

        // 2. 获取方法上的 @TokenSign 注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TokenSign tokenSign = method.getAnnotation(TokenSign.class);

        // 3. 从请求头获取 token
        String token = request.getHeader(CommonConstants.TOKEN_HEADER);
        if (token == null || token.trim().isEmpty()) {
            throw new CommonException("token is null");
        }


        // 5. 构建 Redis key
        String redisKey = CommonConstants.TOKEN + token;

        // 6. 验证 token 是否存在
        Boolean exists = redisTemplate.hasKey(redisKey);
        if (exists == null || !exists) {
            throw new CommonException("token is invalid");
        }

        // 7. 通过token 获取用户信息
        String userInfo = redisTemplate.opsForValue().get(redisKey);
        log.info("用户信息: {}", userInfo);
        UserLoginVO userLoginVO;
        if (userInfo != null && userInfo.startsWith("\"") && userInfo.endsWith("\"")) {
            userInfo = userInfo.substring(1, userInfo.length() - 1);
            userInfo = userInfo.replace("\\\"", "\"");
        }
        userLoginVO = objectMapper.readValue(userInfo, UserLoginVO.class);
        SysUserVO sysUserVO = mapperFactory.getMapperFacade(UserLoginVO.class, SysUserVO.class).map(userLoginVO);

        // 8. 将用户信息保存到 UserContext 中
        //request.setAttribute(CommonConstants.CURRENT_USER_KEY, userInfo);
        UserContext.setCurrentUser(sysUserVO);

        // 9. 执行目标方法
        return joinPoint.proceed();
    }

}
