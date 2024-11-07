package com.reliaquest.api.Exception;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ApiRateLimitException extends RuntimeException {
    private String exceptionIdentificationParam;
    public ApiRateLimitException(String message, String exceptionIdentificationParam){
        super(message);
        this.exceptionIdentificationParam=exceptionIdentificationParam;
    }
}
