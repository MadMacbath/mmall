package com.macbeth.common;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ServerResponse<String> resolveException(BindingResult result){
        String message = Arrays.toString(result.getAllErrors().stream().map(objectError ->
            objectError.getObjectName() +":" + objectError.getDefaultMessage()
        ).toArray());
        return ServerResponse.createByErrorMessage(message);
    }

}
