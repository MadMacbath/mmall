package com.macbeth.common;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ServerResponse resolveException(Exception e){
        String message = e.getMessage();
        return ServerResponse.createByErrorMessage(message);
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
