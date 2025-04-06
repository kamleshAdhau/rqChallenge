package com.reliaquest.api.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    private String id;
    private String employeeName;
    private String title;
    private Integer employeeSalary;
    private Integer employeeAge;
    private String profileImage;
}
