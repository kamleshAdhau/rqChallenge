package com.reliaquest.api.service.impl;

import com.reliaquest.api.utils.EmployeeApiProperties;
import com.reliaquest.api.model.*;
import com.reliaquest.api.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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
        try {
            URI uri = new URI(apiProperties.getBaseUrl() + apiProperties.getAllEmployeeEndpoint());
            ResponseEntity<EmployeeList> response = restTemplate.exchange(uri, HttpMethod.GET, null, EmployeeList.class);
            return response.getBody().getData();
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid URI", e);
        }
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return getAllEmployees().stream()
                .filter(emp -> emp.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "employeeById", key = "#id")
    public Employee getEmployeeById(String id) {
        try {
            URI uri = new URI(apiProperties.getBaseUrl() + apiProperties.getEmployeeByIdEndpoint().replace("{id}", id));
            ResponseEntity<ClientResponse<Employee>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            return response.getBody().getData();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Employee not found for ID: {}", id);
            return null;
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid URI", e);
        }
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

    @CacheEvict(value = {"employees", "employeeById"}, allEntries = true)
    public Employee createEmployee(Map<String, Object> inputMap) {
        EmployeeInput input = new EmployeeInput();
        input.setName((String) inputMap.get("name"));
        input.setSalary(Integer.parseInt((String) inputMap.get("salary")));
        input.setAge(Integer.parseInt((String) inputMap.get("age")));
        input.setTitle((String) inputMap.get("title"));

        try {
            URI uri = new URI(apiProperties.getBaseUrl() + apiProperties.getAllEmployeeEndpoint());
            ResponseEntity<EmployeeResponse> response = restTemplate.exchange(uri, HttpMethod.POST,
                    new HttpEntity<>(input), EmployeeResponse.class);
            return response.getBody().getData();
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid URI", e);
        }
    }

    @CacheEvict(value = {"employees", "employeeById"}, allEntries = true)
    public String deleteEmployee(String id) {
        Employee employee = getEmployeeById(id);
        if (employee == null) return "Employee does not exist";

        DeleteEmployeeInput input = new DeleteEmployeeInput();
        input.setName(employee.getName());

        try {
            URI uri = new URI(apiProperties.getBaseUrl() + apiProperties.getAllEmployeeEndpoint());
            ResponseEntity<DeleteEmployeeResponse<Boolean>> response = restTemplate.exchange(uri, HttpMethod.DELETE,
                    new HttpEntity<>(input), new ParameterizedTypeReference<>() {});
            if (Boolean.TRUE.equals(response.getBody().getData())) {
                return String.format("Employee %s has been deleted", employee.getName());
            }
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid URI", e);
        }

        return "Failed to delete employee";
    }
}
