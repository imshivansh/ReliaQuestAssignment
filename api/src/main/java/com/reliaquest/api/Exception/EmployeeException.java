package com.reliaquest.api.Exception;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
 public class EmployeeException extends RuntimeException{
    private String exceptionIdentificationParam;
    public EmployeeException(String message,String identificationParam) {
        super(message);
        this.exceptionIdentificationParam= identificationParam;
    }
}
