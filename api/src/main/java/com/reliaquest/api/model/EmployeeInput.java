package com.reliaquest.api.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInput {

        @NotBlank
        private String name;

        @Positive
        @NotNull
        private Integer salary;

        @Min(16)
        @Max(75)
        @NotNull private Integer age;

        @NotBlank
        private String title;
}
