package com.reliaquest.api.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "employee-api")
@Data
public class EmployeeApiProperties {
    private String baseUrl;
    private String allEmployeeEndpoint;
    private String employeeByIdEndpoint;
}
