package com.example.authserver.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Sets HTTP status to 500
    public String defaultExceptionHandler(Exception ex, WebRequest request, Model model, HttpServletRequest httpServletRequest) {
        logger.error("An unexpected error occurred during request processing for URI: {}", httpServletRequest.getRequestURI(), ex);

        model.addAttribute("errorMessage", "An unexpected error occurred. We are working to fix it. Please try again later.");
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorTitle", "Something Went Wrong!");

        return "error";
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Sets HTTP status to 400
    public ModelAndView invalidRequestHandler(Exception ex, WebRequest request, Model model, HttpServletRequest httpServletRequest) {
        logger.error("An unexpected error occurred during request processing for URI: {}", httpServletRequest.getRequestURI(), ex);

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorTitle", "Invalid Request!");

        return new ModelAndView("error");
    }

    // Catches validation failures for @ModelAttribute DTOs or @RequestBody DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,WebRequest request, Model model, HttpServletRequest httpServletRequest) {
        logger.error("An unexpected error occurred during request processing for URI: {}", httpServletRequest.getRequestURI(), ex);

        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .sorted()
                .distinct()
                .toList();

        model.addAttribute("errorMessageList", errorMessages);
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorTitle", "Invalid Request!");

        return new ModelAndView("error");
    }

    @ExceptionHandler(RestInvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleRestInvalidRequestException(RestInvalidRequestException ex) {
        logger.error("REST API invalid request received: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(ex.getMessage(),LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public record ErrorResponse(
            String message,
            LocalDateTime timestamp
    ) {}

}
