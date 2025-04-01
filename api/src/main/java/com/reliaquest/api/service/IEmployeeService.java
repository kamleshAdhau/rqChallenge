package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import java.io.IOException;
import java.util.List;

public interface IEmployeeService {

    List<Employee> getAllEmployees() throws IOException;

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployee();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(String name, String salary, String age);

    String deleteEmployee(String id);
}
