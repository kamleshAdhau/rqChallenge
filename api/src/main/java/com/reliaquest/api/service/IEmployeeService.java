package com.reliaquest.api.service;

import java.io.IOException;
import java.util.List;

import com.reliaquest.api.model.Employee;

public interface IEmployeeService {

    List<Employee> getAllEmployees() throws IOException;

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(String name, String salary, String age);

    String deleteEmployee(String id);
}