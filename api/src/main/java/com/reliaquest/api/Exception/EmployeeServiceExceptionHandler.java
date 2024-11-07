package com.reliaquest.api.Exception;

import com.reliaquest.api.constants.ApplicationConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@ControllerAdvice
public class EmployeeServiceExceptionHandler {

    @ExceptionHandler(EmployeeException.class)
    public ResponseEntity<?>handleEmployeeNotExistException(EmployeeException exception){
        log.error(exception.getMessage());
         ErrorResponse<String> errorResponse = new ErrorResponse<>(exception.getMessage(), exception.getExceptionIdentificationParam());
         return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<?>handleExternalApiException(ExternalApiException exception){
        log.error(exception.getMessage());
        ErrorResponse<String>errorResponse =  new ErrorResponse<>(exception.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.SERVICE_UNAVAILABLE);

    }

    @ExceptionHandler(ApiRateLimitException.class)
    public ResponseEntity<?>handleApiRateLimitException(ApiRateLimitException exception){
        log.error(exception.getMessage());
        ErrorResponse<String>errorResponse =  new ErrorResponse<>(exception.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.TOO_MANY_REQUESTS);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });


        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }



}
