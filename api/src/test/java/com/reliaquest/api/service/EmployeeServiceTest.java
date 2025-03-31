package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeList;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.service.impl.EmployeeService;
import com.reliaquest.api.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EmployeeServiceTest {

    private final List<Employee> employeeList = new ArrayList<>();
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService = new EmployeeService();

    @BeforeEach
    public void setup() {
        employeeList.add(Employee.builder().id(1).employee_name("Ranjit").employee_age(23).employee_salary(1000).build());
        employeeList.add(Employee.builder().id(2).employee_name("Bradley").employee_age(25).employee_salary(1002).build());
        employeeList.add(Employee.builder().id(3).employee_name("Tiger").employee_age(26).employee_salary(1004).build());
        employeeList.add(Employee.builder().id(4).employee_name("Nixon").employee_age(27).employee_salary(100001).build());
        employeeList.add(Employee.builder().id(5).employee_name("Kennedy").employee_age(27).employee_salary(100006).build());
        employeeList.add(Employee.builder().id(6).employee_name("Haley").employee_age(27).employee_salary(10000).build());
        employeeList.add(Employee.builder().id(7).employee_name("Doris").employee_age(27).employee_salary(10001).build());
        employeeList.add(Employee.builder().id(8).employee_name("Vance").employee_age(27).employee_salary(10002).build());
        employeeList.add(Employee.builder().id(9).employee_name("Caesar").employee_age(27).employee_salary(10003).build());
        employeeList.add(Employee.builder().id(10).employee_name("Yuri").employee_age(27).employee_salary(10004).build());
        employeeList.add(Employee.builder().id(11).employee_name("Jenette").employee_age(27).employee_salary(10005).build());
    }

    @Test
    public void testGetAllEmployees() throws URISyntaxException, IOException {
        getAllEmployee();

        List<Employee> allEmployeesList = employeeService.getAllEmployees();

        assertEquals(allEmployeesList.size(), employeeList.size());
        assertEquals(allEmployeesList, employeeList);

    }

    @Test
    public void testGetEmployeesByNameSearch() throws URISyntaxException, IOException {
        getAllEmployee();

        List<Employee> allEmployeesList = employeeService.getEmployeesByNameSearch("Ran");

        assertEquals(allEmployeesList.get(0).getEmployee_name(), "Ranjit");
    }

    @Test
    public void testGetEmployeeById() throws URISyntaxException, IOException {
        EmployeeResponse employeeResponse = getEmployeeByID();

        Employee employee = employeeService.getEmployeeById("1");

        assertEquals(employeeResponse.getData(), employee);
    }

    private EmployeeResponse getEmployeeByID() {
        String id = "1";
        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .data(Employee.builder().id(1)
                        .employee_name("Ranjit")
                        .employee_age(23)
                        .employee_salary(1000)
                        .build())
                .build();

        when(restTemplate.exchange(
                Constants.GET_EMPLOYEE_ID_URL,
                HttpMethod.GET,
                null,
                EmployeeResponse.class,
                id))
                .thenReturn(new ResponseEntity<>(employeeResponse, HttpStatus.OK));
        return employeeResponse;
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws URISyntaxException, IOException {
        getAllEmployee();

        Integer highestSalaryOfEmployee = employeeService.getHighestSalaryOfEmployees();

        assertEquals(highestSalaryOfEmployee, Integer.valueOf(100006));
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws URISyntaxException, IOException {
        getAllEmployee();

        List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(topTenHighestEarningEmployeeNames.contains("Ranjit"), false);
        assertEquals(topTenHighestEarningEmployeeNames.contains("Jenette"), true);
        assertEquals(topTenHighestEarningEmployeeNames.size(), 10);
    }

    @Test
    public void testCreateEmployee() throws URISyntaxException, IOException {
        Employee employee = Employee.builder()
                .employee_name("Byrd")
                .employee_salary(1004)
                .employee_age(29)
                .build();

        EmployeeResponse employeeResponse = EmployeeResponse.builder().data(employee).status("Success").build();

        when(restTemplate.exchange(
                Constants.CREATE_EMPLOYEE_URL,
                HttpMethod.POST,
                new HttpEntity<>(employee),
                EmployeeResponse.class))
                .thenReturn(new ResponseEntity<>(employeeResponse, HttpStatus.OK));

        Employee serviceEmployee = employeeService.createEmployee("Byrd", "1004", "29");
        assertEquals(serviceEmployee, employee);
        assertEquals(serviceEmployee, employee);

    }

    @Test
    public void testDeleteEmployee() throws URISyntaxException, IOException {
        String id = "1";
        EmployeeResponse employeeResponse = getEmployeeByID();

        when(restTemplate.exchange(
                Constants.DELETE_EMPLOYEE_URL,
                HttpMethod.DELETE,
                null,
                EmployeeResponse.class,
                id))
                .thenReturn(new ResponseEntity<>(employeeResponse, HttpStatus.OK));

        String employeeName = employeeService.deleteEmployee("1");

        assertEquals(employeeName, "Ranjit");

    }

    private void getAllEmployee() throws URISyntaxException {
        EmployeeList employeeList = new EmployeeList(this.employeeList);
        when(restTemplate.exchange(
                new URI(Constants.GET_EMPLOYEE_URL),
                HttpMethod.GET,
                null,
                EmployeeList.class))
                .thenReturn(new ResponseEntity<>(employeeList, HttpStatus.OK));
    }
}