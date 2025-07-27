package com.example.clientapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Sets HTTP status to 500
    public String handleAllUncaughtException(Exception ex, WebRequest request, Model model, HttpServletRequest httpServletRequest) {
        logger.error("An unexpected error occurred during request processing for URI: {}", httpServletRequest.getRequestURI(), ex);

        model.addAttribute("errorMessage", "An unexpected error occurred. We are working to fix it. Please try again later.");
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorTitle", "Something Went Wrong!");

        return "error";
    }
}