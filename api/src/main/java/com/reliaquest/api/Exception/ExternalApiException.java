package com.reliaquest.api.Exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ExternalApiException extends  RuntimeException{
    private String message;
    public ExternalApiException(String message){
        super(message);
    }
}
