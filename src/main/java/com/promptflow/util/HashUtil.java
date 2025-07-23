package com.promptflow.util;

import com.promptflow.dto.PromptRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    
    /**
     * 根据PromptRequest生成唯一的哈希值
     * @param request 提示词请求对象
     * @return MD5哈希值
     */
    public static String generateRequestHash(PromptRequest request) {
        if (request == null) {
            return null;
        }
        
        // 构建用于哈希的字符串，将所有参数按固定顺序拼接
        StringBuilder sb = new StringBuilder();
        sb.append(nullToEmpty(request.getTaskDescription()));
        sb.append("|");
        sb.append(nullToEmpty(request.getTargetAudience()));
        sb.append("|");
        sb.append(nullToEmpty(request.getOutputFormat()));
        sb.append("|");
        sb.append(nullToEmpty(request.getConstraints()));
        sb.append("|");
        sb.append(nullToEmpty(request.getExamples()));
        sb.append("|");
        sb.append(nullToEmpty(request.getTone()));
        sb.append("|");
        sb.append(nullToEmpty(request.getLength()));
        
        String input = sb.toString();
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
    
    /**
     * 将null值转换为空字符串
     * @param str 输入字符串
     * @return 非null字符串
     */
    private static String nullToEmpty(String str) {
        return str == null ? "" : str.trim();
    }
    
    /**
     * 验证两个请求是否相同
     * @param request1 请求1
     * @param request2 请求2
     * @return 是否相同
     */
    public static boolean isSameRequest(PromptRequest request1, PromptRequest request2) {
        if (request1 == null && request2 == null) {
            return true;
        }
        if (request1 == null || request2 == null) {
            return false;
        }
        
        String hash1 = generateRequestHash(request1);
        String hash2 = generateRequestHash(request2);
        
        return hash1 != null && hash1.equals(hash2);
    }
}