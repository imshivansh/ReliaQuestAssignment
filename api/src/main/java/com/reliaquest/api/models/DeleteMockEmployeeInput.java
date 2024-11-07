package com.reliaquest.api.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

    @Data
    @AllArgsConstructor
    public class DeleteMockEmployeeInput {
        @NotBlank
        private String name;
    }

