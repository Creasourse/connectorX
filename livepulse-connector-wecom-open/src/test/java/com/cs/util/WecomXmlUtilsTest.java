package com.cs.util;

import com.cs.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @ClassName: WecomXmlUtilsTest
 * @Author: wwd
 * @TODO: 企业微信XML解析工具测试
 * @Date: 2026/3/27
 */
@DisplayName("企业微信XML解析工具测试")
public class WecomXmlUtilsTest extends BaseTest {

    @Test
    @DisplayName("测试解析XML - 成功")
    public void testParseXml_Success() {
        String xml = """
                <xml>
                    <ToUserName>corp</ToUserName>
                    <FromUserName>user</FromUserName>
                    <CreateTime>1234567890</CreateTime>
                    <MsgType>event</MsgType>
                    <Event>change_contact</Event>
                    <ChangeType>create_user</ChangeType>
                    <UserID>zhangsan</UserID>
                    <Name>张三</Name>
                </xml>
                """;

        var result = WecomXmlUtils.parseXml(xml);

        Assertions.assertNotNull(result, "解析结果不应为空");
        Assertions.assertEquals("corp", result.getToUserName(), "ToUserName应该匹配");
        Assertions.assertEquals("user", result.getFromUserName(), "FromUserName应该匹配");
        Assertions.assertEquals(1234567890L, result.getCreateTime(), "CreateTime应该匹配");
        Assertions.assertEquals("event", result.getMsgType(), "MsgType应该匹配");
        Assertions.assertEquals("change_contact", result.getEvent(), "Event应该匹配");
        Assertions.assertEquals("create_user", result.getChangeType(), "ChangeType应该匹配");
        Assertions.assertEquals("zhangsan", result.getUserId(), "UserID应该匹配");
        Assertions.assertEquals("张三", result.getName(), "Name应该匹配");
    }

    @Test
    @DisplayName("测试解析XML为Map - 成功")
    public void testParseXmlToMap_Success() {
        String xml = """
                <xml>
                    <ToUserName>corp</ToUserName>
                    <FromUserName>user</FromUserName>
                    <CreateTime>1234567890</CreateTime>
                </xml>
                """;

        var result = WecomXmlUtils.parseXmlToMap(xml);

        Assertions.assertNotNull(result, "解析结果不应为空");
        Assertions.assertEquals("corp", result.get("ToUserName"), "ToUserName应该匹配");
        Assertions.assertEquals("user", result.get("FromUserName"), "FromUserName应该匹配");
        Assertions.assertEquals("1234567890", result.get("CreateTime"), "CreateTime应该匹配");
    }

    @Test
    @DisplayName("测试解析空XML - 抛异常")
    public void testParseXml_Empty() {
        String xml = "";

        Assertions.assertThrows(RuntimeException.class, () -> {
            WecomXmlUtils.parseXml(xml);
        });
    }

    @Test
    @DisplayName("测试解析格式错误的XML - 抛异常")
    public void testParseXml_InvalidFormat() {
        String xml = "<invalid><xml>";

        Assertions.assertThrows(RuntimeException.class, () -> {
            WecomXmlUtils.parseXml(xml);
        });
    }
}