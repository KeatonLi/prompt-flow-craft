package com.promptflow.util;

import com.promptflow.dto.PromptRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String generateRequestHash(PromptRequest request) {
        if (request == null) {
            return null;
        }

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

    private static String nullToEmpty(String str) {
        return str == null ? "" : str.trim();
    }
}
