package com.garage.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(
            ResourceNotFoundException exception,
            Model model
    ) {
        model.addAttribute(
                "errorMessage",
                exception.getMessage()
        );
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(
            Exception exception,
            Model model
    ) {
        exception.printStackTrace();
        model.addAttribute(
                "errorMessage",
                exception.getMessage() != null ? exception.getMessage() : "Đã xảy ra lỗi trong hệ thống."
        );
        return "error/500";
    }
}