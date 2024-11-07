package com.reliaquest.api.Exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ErrorResponse <T>{
    private T message;
    private T errorIdentificationParam;

    public ErrorResponse(T message){
        this.message= message;
    }


}
