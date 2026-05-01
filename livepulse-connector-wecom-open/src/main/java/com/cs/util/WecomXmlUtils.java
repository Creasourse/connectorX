package com.cs.util;

import com.cs.dto.WecomCallbackRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: WecomXmlUtils
 * @Author: wwd
 * @TODO: 企业微信XML解析工具类
 * @Date: 2026/3/25
 */
@Slf4j
public class WecomXmlUtils {

    /**
     * 解析企业微信XML回调数据
     *
     * @param xml XML字符串
     * @return 解析后的DTO
     */
    public static WecomCallbackRequestDto parseXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new StringBufferInputStream(xml));

            Element root = document.getDocumentElement();
            WecomCallbackRequestDto dto = new WecomCallbackRequestDto();

            dto.setToUserName(getTagValue(root, "ToUserName"));
            dto.setFromUserName(getTagValue(root, "FromUserName"));
            dto.setCreateTime(parseLong(getTagValue(root, "CreateTime")));
            dto.setMsgType(getTagValue(root, "MsgType"));
            dto.setEvent(getTagValue(root, "Event"));
            dto.setChangeType(getTagValue(root, "ChangeType"));
            dto.setUserId(getTagValue(root, "UserID"));
            dto.setName(getTagValue(root, "Name"));
            dto.setDepartment(getTagValue(root, "Department"));
            dto.setPosition(getTagValue(root, "Position"));
            dto.setMobile(getTagValue(root, "Mobile"));
            dto.setGender(getTagValue(root, "Gender"));
            dto.setEmail(getTagValue(root, "Email"));
            dto.setStatus(parseInteger(getTagValue(root, "Status")));
            dto.setEnable(parseInteger(getTagValue(root, "Enable")));
            dto.setExternalUserId(getTagValue(root, "ExternalUserID"));
            dto.setChatId(getTagValue(root, "ChatId"));

            return dto;
        } catch (Exception e) {
            log.error("解析企业微信XML失败", e);
            throw new RuntimeException("解析XML失败: " + e.getMessage());
        }
    }

    /**
     * 解析XML为Map
     *
     * @param xml XML字符串
     * @return Map格式的数据
     */
    public static Map<String, String> parseXmlToMap(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new StringBufferInputStream(xml));

            Element root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            Map<String, String> map = new HashMap<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    map.put(element.getNodeName(), element.getTextContent());
                }
            }

            return map;
        } catch (Exception e) {
            log.error("解析企业微信XML为Map失败", e);
            throw new RuntimeException("解析XML失败: " + e.getMessage());
        }
    }

    /**
     * 获取标签值
     */
    private static String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * 解析Long
     */
    private static Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析Integer
     */
    private static Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}