package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        employeeList.add(Employee.builder()
                .id("1")
                .employeeName("Ranjit")
                .employeeAge(23)
                .employeeSalary(1000)
                .build());
        employeeList.add(Employee.builder()
                .id("2")
                .employeeName("Bradley")
                .employeeAge(25)
                .employeeSalary(1002)
                .build());
        employeeList.add(Employee.builder()
                .id("3")
                .employeeName("Tiger")
                .employeeAge(26)
                .employeeSalary(1004)
                .build());
        employeeList.add(Employee.builder()
                .id("4")
                .employeeName("Nixon")
                .employeeAge(27)
                .employeeSalary(100001)
                .build());
        employeeList.add(Employee.builder()
                .id("5")
                .employeeName("Kennedy")
                .employeeAge(27)
                .employeeSalary(100006)
                .build());
        employeeList.add(Employee.builder()
                .id("6")
                .employeeName("Haley")
                .employeeAge(27)
                .employeeSalary(10000)
                .build());
        employeeList.add(Employee.builder()
                .id("7")
                .employeeName("Doris")
                .employeeAge(27)
                .employeeSalary(10001)
                .build());
        employeeList.add(Employee.builder()
                .id("8")
                .employeeName("Vance")
                .employeeAge(27)
                .employeeSalary(10002)
                .build());
        employeeList.add(Employee.builder()
                .id("9")
                .employeeName("Caesar")
                .employeeAge(27)
                .employeeSalary(10003)
                .build());
        employeeList.add(Employee.builder()
                .id("10")
                .employeeName("Yuri")
                .employeeAge(27)
                .employeeSalary(10004)
                .build());
        employeeList.add(Employee.builder()
                .id("11")
                .employeeName("Jenette")
                .employeeAge(27)
                .employeeSalary(10005)
                .build());
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

        assertEquals(allEmployeesList.get(0).getEmployeeName(), "Ranjit");
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
                .data(Employee.builder()
                        .id("1")
                        .employeeName("Ranjit")
                        .employeeAge(23)
                        .employeeSalary(1000)
                        .build())
                .build();

        when(restTemplate.exchange(Constants.GET_EMPLOYEE_ID_URL, HttpMethod.GET, null, EmployeeResponse.class, id))
                .thenReturn(new ResponseEntity<>(employeeResponse, HttpStatus.OK));
        return employeeResponse;
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws URISyntaxException, IOException {
        getAllEmployee();

        Integer highestSalaryOfEmployee = employeeService.getHighestSalaryOfEmployee();

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
                .employeeName("Byrd")
                .employeeSalary(1004)
                .employeeAge(29)
                .build();

//        EmployeeInput employeeInput=EmployeeInput.builder().name("Byrd").salary(1000).age(29).title("Manager").build();


        EmployeeResponse employeeResponse =
                EmployeeResponse.builder().data(employee).status("Success").build();

        when(restTemplate.exchange(
                        Constants.GET_EMPLOYEE_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(employee),
                        EmployeeResponse.class))
                .thenReturn(new ResponseEntity<>(employeeResponse, HttpStatus.OK));

        Map<String,Object> employeeInput= new HashMap<>();
        employeeInput.put("name","Byrd");
        employeeInput.put("salary","1004");
        employeeInput.put("age","29");
        employeeInput.put("title","Manager");
        Employee serviceEmployee = employeeService.createEmployee(employeeInput);
        assertEquals(serviceEmployee, employee);
    }

    @Test
    public void testDeleteEmployee() throws URISyntaxException, IOException {
        String id = "1";
        EmployeeResponse employeeResponse = getEmployeeByID();

        when(restTemplate.exchange(Constants.GET_EMPLOYEE_URL, HttpMethod.DELETE, null, EmployeeResponse.class, id))
                .thenReturn(new ResponseEntity<>(employeeResponse, HttpStatus.OK));

        String employeeName = employeeService.deleteEmployee("1");

        assertEquals(employeeName, "Ranjit");
    }

    private void getAllEmployee() throws URISyntaxException {
        EmployeeList employeeList = new EmployeeList(this.employeeList);
        when(restTemplate.exchange(new URI(Constants.GET_EMPLOYEE_URL), HttpMethod.GET, null, EmployeeList.class))
                .thenReturn(new ResponseEntity<>(employeeList, HttpStatus.OK));
    }
}
