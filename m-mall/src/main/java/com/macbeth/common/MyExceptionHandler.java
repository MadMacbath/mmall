package com.macbeth.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ServerResponse resolveException(Exception e){
        log.error("统一捕获异常：{}",e);
        return ServerResponse.createByErrorMessage("服务器异常");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ServerResponse handlerException(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        String message = Arrays.toString(result.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage())
                .collect(Collectors.toList()).toArray());
        return ServerResponse.createByErrorMessage(message);
    }

}
