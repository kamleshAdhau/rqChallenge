package com.reliaquest.api.service.impl;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeList;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService implements IEmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees from external service.");

        ResponseEntity<EmployeeList> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(
                    new URI(Constants.GET_EMPLOYEE_URL), HttpMethod.GET, null, EmployeeList.class);
        } catch (URISyntaxException e) {
            log.error("Error while fetching all employee details: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        log.info(
                "Successfully retrieved {} employees",
                responseEntity.getBody().getData().size());
        return responseEntity.getBody().getData();
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> employeeList = null;
        try {
            employeeList = getAllEmployees();
        } catch (Exception e) {
            log.error("Error while searching employee details by Name: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        List<Employee> result = employeeList.stream()
                .filter(employee -> employee.getEmployeeName().contains(searchString))
                .collect(Collectors.toList());
        log.info("Found {} employees matching search criteria.", result.size());
        return result;
    }

    public Employee getEmployeeById(String id) {
        ResponseEntity<EmployeeResponse> responseEntity =
                restTemplate.exchange(Constants.GET_EMPLOYEE_ID_URL, HttpMethod.GET, null, EmployeeResponse.class, id);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Successfully retrieved employee details with ID: {}", id);
        } else {
            log.warn("Failed to retrieve employee with ID: {}", id);
        }

        return responseEntity.getBody().getData();
    }

    public Integer getHighestSalaryOfEmployee() {
        log.info("Fetching highest salary among employees.");
        List<Employee> employeeList = null;
        try {
            employeeList = getAllEmployees();
        } catch (Exception e) {
            log.error("Error fetching employee while calculating highest salary: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        Integer highestSalary = employeeList.stream()
                .max(Comparator.comparing(Employee::getEmployeeSalary))
                .get()
                .getEmployeeSalary();
        log.info("Highest salary among employees is: {}", highestSalary);
        return highestSalary;
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.info("Fetching top 10 highest earning employee names.");
        List<Employee> employeeList = null;
        try {
            employeeList = getAllEmployees();
        } catch (Exception e) {
            log.error("Error while calculating top earners: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        List<String> topTenEmployeeNames = employeeList.stream()
                .sorted(Comparator.comparing(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
        log.info("Top 10 highest earning employees: {}", topTenEmployeeNames);
        return topTenEmployeeNames;
    }


    public Employee createEmployee(Map<String, Object> employeeInput) {
        String name = (String) employeeInput.get("name");
        String salary = (String) employeeInput.get("salary");
        String age = (String) employeeInput.get("age");
        String title = (String) employeeInput.get("title");
        log.info("Creating new employee with name: {}, salary: {}, age: {}, title: {}", name, salary, age, title);
        EmployeeInput input = new EmployeeInput();
        input.setName(name);
        input.setSalary(Integer.parseInt(salary));
        input.setAge(Integer.parseInt(age));
        input.setTitle(title);

        ResponseEntity<EmployeeResponse> employeeResponseResponseEntity = restTemplate.exchange(
                Constants.GET_EMPLOYEE_URL, HttpMethod.POST, new HttpEntity<>(input), EmployeeResponse.class);

        if (employeeResponseResponseEntity.getStatusCode() == HttpStatus.CREATED) {
            log.info("Successfully created employee: {}", name);
        } else {
            log.warn("Failed to create employee with name: {}", name);
        }

        return employeeResponseResponseEntity.getBody().getData();
    }

    public String deleteEmployee(String id) {

        Employee employee = getEmployeeById(id);

        ResponseEntity<EmployeeResponse> employeeResponseResponseEntity = restTemplate.exchange(
                Constants.GET_EMPLOYEE_URL, HttpMethod.DELETE, null, EmployeeResponse.class, id);

        if (employeeResponseResponseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Successfully deleted employee with name: {}", employee.getEmployeeName());
            return employee.getEmployeeName();
        } else {
            log.warn("Failed to delete employee with ID: {}", id);
            return "Employee does not exist";
        }
    }
}
