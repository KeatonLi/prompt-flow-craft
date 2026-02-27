package com.promptflow.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * JSON 工具类
 */
public class JsonUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将 JSON 字符串解析为字符串列表
     */
    public static List<String> parseStringList(String json) {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            logger.warn("解析 JSON 字符串失败: {}", json, e);
            return List.of();
        }
    }
    
    /**
     * 将对象转换为 JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.warn("转换对象为 JSON 失败", e);
            return null;
        }
    }
    
    /**
     * 将 JSON 字符串转换为对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.warn("解析 JSON 字符串失败: {}", json, e);
            return null;
        }
    }
}
