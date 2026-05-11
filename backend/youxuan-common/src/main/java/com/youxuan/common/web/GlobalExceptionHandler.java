package com.youxuan.common.web;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.exception.BizException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 通过 {@link RestControllerAdvice} 统一捕获 Controller 层抛出的各类异常，
 * 转换为统一的 {@link ApiResponse} 格式返回给前端，避免将异常堆栈直接暴露给调用方。
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * <p>根据 ErrorCode 中定义的 HTTP 状态码和提示信息返回响应。</p>
     *
     * @param exception 业务异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBizException(BizException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), exception.getMessage(), RequestContext.getRequestId()));
    }

    /**
     * 处理 @RequestBody 参数校验异常
     * <p>当使用 @Valid 注解校验请求体失败时触发，收集所有字段校验失败信息。</p>
     *
     * @param exception 方法参数校验异常
     * @return 参数不合法响应（HTTP 400）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID.getCode(), buildBindMessage(exception), RequestContext.getRequestId()));
    }

    /**
     * 处理 @ModelAttribute 或普通参数绑定异常
     * <p>当请求参数绑定到 Java 对象失败时触发。</p>
     *
     * @param exception 参数绑定异常
     * @return 参数不合法响应（HTTP 400）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException exception) {
        // 收集所有字段错误信息，用分号拼接
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID.getCode(), message, RequestContext.getRequestId()));
    }

    /**
     * 处理未捕获的未知异常（兜底处理器）
     * <p>记录错误日志，返回 500 内部错误，避免将异常堆栈泄露给前端。</p>
     *
     * @param exception 未知异常
     * @return 服务端异常响应（HTTP 500）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        LOGGER.error("未捕获的异常", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR, RequestContext.getRequestId()));
    }

    /**
     * 构建参数校验错误信息
     * <p>遍历 BindingResult 中的字段错误，拼接成易读的文本：字段名 + 错误描述，多个错误用分号分隔。</p>
     *
     * @param exception @RequestBody 参数校验异常
     * @return 拼接后的错误描述字符串
     */
    private String buildBindMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }
}
