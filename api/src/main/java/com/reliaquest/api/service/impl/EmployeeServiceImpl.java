package com.reliaquest.api.service.impl;

import com.reliaquest.api.model.*;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.utils.EmployeeApiProperties;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmployeeApiProperties apiProperties;

    @Cacheable("employees")
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees from external API.");
        URI uri = URI.create(apiProperties.getBaseUrl() + apiProperties.getAllEmployeeEndpoint());
        ResponseEntity<EmployeeList> response = restTemplate.exchange(uri, HttpMethod.GET, null, EmployeeList.class);
        if (response.getBody() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "External service returned empty response");
        }
        return response.getBody().getData();
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return getAllEmployees().stream()
                .filter(emp -> emp.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        URI uri = URI.create(apiProperties.getBaseUrl()
                + apiProperties.getEmployeeByIdEndpoint().replace("{id}", id));
        ResponseEntity<ApiResponse<Employee>> response =
                restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        if (response.getBody() == null || response.getBody().getData() == null) {
            log.warn("No employee found or null response for ID: {}", id);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "External service returned empty response");
        }
        return response.getBody().getData();
    }

    public Integer getHighestSalaryOfEmployee() {
        return getAllEmployees().stream()
                .max(Comparator.comparing(Employee::getSalary))
                .map(Employee::getSalary)
                .orElse(0);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    @CacheEvict(
            value = {"employees"},
            allEntries = true)
    public Employee createEmployee(Map<String, Object> inputMap) {
        EmployeeInput input = new EmployeeInput();
        input.setName((String) inputMap.get("name"));
        input.setSalary(Integer.parseInt((String) inputMap.get("salary")));
        input.setAge(Integer.parseInt((String) inputMap.get("age")));
        input.setTitle((String) inputMap.get("title"));

        URI uri = URI.create(apiProperties.getBaseUrl() + apiProperties.getAllEmployeeEndpoint());
        ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                uri, HttpMethod.POST, new HttpEntity<>(input), new ParameterizedTypeReference<>() {});
        if (response.getBody() == null || response.getBody().getData() == null) {
            log.warn("Unable to create employee");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "External service returned empty response");
        }
        return response.getBody().getData();
    }

    @CacheEvict(
            value = {"employees"},
            allEntries = true)
    public String deleteEmployee(String id) {
        Employee employee = getEmployeeById(id);
        if (employee == null) return "Employee does not exist";

        DeleteEmployeeInput input = new DeleteEmployeeInput();
        input.setName(employee.getName());

        URI uri = URI.create(apiProperties.getBaseUrl() + apiProperties.getAllEmployeeEndpoint());
        ResponseEntity<ApiResponse<Boolean>> response = restTemplate.exchange(
                uri, HttpMethod.DELETE, new HttpEntity<>(input), new ParameterizedTypeReference<>() {});
        if (Boolean.TRUE.equals(response.getBody().getData())) {
            return String.format("Employee %s has been deleted", employee.getName());
        }
        return "Failed to delete employee";
    }
}
