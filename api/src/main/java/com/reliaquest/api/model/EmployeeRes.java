package com.reliaquest.api.model;

import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRes {

    private UUID id;
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;
}
