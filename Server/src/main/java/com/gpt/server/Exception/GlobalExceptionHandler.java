package com.gpt.server.Exception;

import com.gpt.server.Common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Exception
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:53
 * @Description: 全局异常处理类
 * @Version: 1.0
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 定义异常处理的handeler
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        e.printStackTrace();  // 错误异常堆栈信息先打印
        log.error("服务器发生异常：{}", e.getMessage());
        log.error("异常详情：", e);

        return Result.error("服务器发生异常");
    }
}
