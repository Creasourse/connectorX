package com.cs.controller;

import com.cs.dto.WecomCallbackRequestDto;
import com.cs.exception.CommonException;
import com.cs.param.WecomCallbackLogPageParam;
import com.cs.param.WecomSyncLogPageParam;
import com.cs.resp.PageResult;
import com.cs.resp.RespResult;
import com.cs.service.WecomCallbackConfigService;
import com.cs.service.WecomCallbackLogService;
import com.cs.service.WecomSyncLogService;
import com.cs.util.WecomXmlUtils;
import com.cs.vo.WecomCallbackLogVO;
import com.cs.vo.WecomSyncLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @ClassName: WecomCallbackController
 * @Author: wwd
 * @TODO: 企业微信回调接口控制器
 * @Date: 2026/3/25
 */
@Slf4j
@RestController
@RequestMapping("/wecomCallback")
@Tag(name = "企业微信回调接口")
public class WecomCallbackController {

    @Autowired
    private WecomCallbackLogService wecomCallbackLogService;

    @Autowired
    private WecomCallbackConfigService wecomCallbackConfigService;

    @Autowired
    private WecomSyncLogService wecomSyncLogService;

    /**
     * 接收企业微信回调
     * 企业微信将以POST请求发送XML格式数据到该接口
     *
     * @param request HTTP请求
     * @return 响应企业微信需要返回 "success"
     */
    @PostMapping("/receive")
    @Operation(summary = "接收企业微信回调")
    public String receiveCallback(HttpServletRequest request) {
        try {
            // 读取请求体
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                requestBody.append(line);
            }

            String requestBodyStr = requestBody.toString();
            log.info("接收到企业微信回调: {}", requestBodyStr);

            // 解析XML数据
            WecomCallbackRequestDto callbackDto = WecomXmlUtils.parseXml(requestBodyStr);

            // 处理回调
            wecomCallbackLogService.handleCallback(callbackDto, requestBodyStr);

            // 企业微信要求返回 "success"
            return "success";
        } catch (Exception e) {
            log.error("处理企业微信回调失败", e);
            // 即使失败也要返回 success，避免企业微信重复推送
            return "success";
        }
    }

    /**
     * 验证URL接口（GET）- 企业微信验证URL有效性
     * 企业微信在配置回调URL时会发送GET请求验证
     *
     * @param msg         随机字符串
     * @param msgEncrypt  加密字符串
     * @param signature   签名
     * @return 验证结果（解密后的字符串）
     */
    @RequestMapping(value = "/callback", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "企业微信回调接口（验证+消息接收）")
    public String handleCallback(
            @RequestParam(name = "msg_signature", required = true) String msgSignature,
            @RequestParam(name = "timestamp", required = true) Integer timestamp,
            @RequestParam(name = "nonce", required = true) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr,
            HttpServletRequest request) {
        try {
            String method = request.getMethod();
            log.info("接收到企业微信{}请求: signature={}, timestamp:{}, nonce:{}, echostr:{}", method, msgSignature, timestamp, nonce, echostr);

            // GET 请求：验证URL
            if ("GET".equalsIgnoreCase(method)) {
                if (echostr == null || echostr.isEmpty()) {
                    log.error("GET请求缺少必需参数echostr");
                    return "error";
                }
                String echoStr = wecomCallbackConfigService.verifyUrl(msgSignature, timestamp, nonce, echostr);
                log.info("验证回调URL成功，返回解密字符串: {}", echoStr);
                return echoStr;
            }

            // POST 请求：接收并解密业务消息
            if ("POST".equalsIgnoreCase(method)) {
                // 读取POST请求体
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = request.getReader().readLine()) != null) {
                    requestBody.append(line);
                }
                String requestBodyStr = requestBody.toString();
                log.info("接收到企业微信POST消息: {}", requestBodyStr);

                // TODO: 解密POST消息
                // 1. 从msg_encrypt参数或请求体中获取加密消息
                // 2. 使用WXBizMsgCrypt解密
                // 3. 解析解密后的XML消息
                // 4. 处理业务逻辑

                // 临时解析方式（直接解析XML）
                WecomCallbackRequestDto callbackDto = WecomXmlUtils.parseXml(requestBodyStr);
                wecomCallbackLogService.handleCallback(callbackDto, requestBodyStr);

                // 企业微信要求返回 "success"
                return "success";
            }

            return "error";
        } catch (Exception e) {
            log.error("处理企业微信回调失败", e);
            // 即使失败也要返回 success，避免企业微信重复推送（POST请求）
            return e.getMessage();
        }
    }

    /**
     * 手动触发回调测试
     *
     * @param callbackDto 回调数据
     * @return 处理结果
     */
    @PostMapping("/test")
    @Operation(summary = "手动触发回调测试")
    public RespResult<Boolean> testCallback(@RequestBody WecomCallbackRequestDto callbackDto) {
        try {
            String requestBody = "Test callback";
            wecomCallbackLogService.handleCallback(callbackDto, requestBody);
            return RespResult.success(true);
        } catch (CommonException e) {
            log.error("测试回调失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    /**
     * 分页查询回调日志
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @PostMapping("/log/pageList")
    @Operation(summary = "分页查询回调日志")
    public RespResult<PageResult<WecomCallbackLogVO>> pageList(@RequestBody WecomCallbackLogPageParam pageParam) {
        try {
            return RespResult.success(wecomCallbackLogService.pageList(pageParam));
        } catch (Exception e) {
            log.error("分页查询回调日志失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    /**
     * 根据ID查询回调日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    @GetMapping("/log/detail/{id}")
    @Operation(summary = "查询回调日志详情")
    public RespResult<WecomCallbackLogVO> getDetailById(@PathVariable("id") Long id) {
        try {
            return RespResult.success(wecomCallbackLogService.getDetailById(id));
        } catch (Exception e) {
            log.error("查询回调日志详情失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    /**
     * 分页查询同步日志
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @PostMapping("/syncLog/pageList")
    @Operation(summary = "分页查询同步日志")
    public RespResult<PageResult<WecomSyncLogVO>> pageSyncLogList(@RequestBody WecomSyncLogPageParam pageParam) {
        try {
            return RespResult.success(wecomSyncLogService.pageList(pageParam));
        } catch (Exception e) {
            log.error("分页查询同步日志失败", e);
            return RespResult.failure(e.getMessage());
        }
    }

    /**
     * 根据ID查询同步日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    @GetMapping("/syncLog/detail/{id}")
    @Operation(summary = "查询同步日志详情")
    public RespResult<WecomSyncLogVO> getSyncLogDetailById(@PathVariable("id") Long id) {
        try {
            return RespResult.success(wecomSyncLogService.getDetailById(id));
        } catch (Exception e) {
            log.error("查询同步日志详情失败", e);
            return RespResult.failure(e.getMessage());
        }
    }
}