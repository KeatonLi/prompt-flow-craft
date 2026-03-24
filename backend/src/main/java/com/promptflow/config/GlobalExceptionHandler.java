package com.promptflow.config;

import com.promptflow.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理 Controller 层抛出的所有异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("参数错误: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, e.getMessage()));
    }
    
    /**
     * 处理运行时异常（业务异常）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        logger.error("业务处理异常", e);
        return ResponseEntity.internalServerError()
            .body(ApiResponse.error(500, e.getMessage()));
    }
    
    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        logger.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(500, "服务器内部错误: " + e.getMessage()));
    }
}