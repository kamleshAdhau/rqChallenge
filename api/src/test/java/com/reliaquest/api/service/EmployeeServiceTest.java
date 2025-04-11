package com.reliaquest.api.service;

import com.reliaquest.api.model.ClientResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeList;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import com.reliaquest.api.utils.EmployeeApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EmployeeServiceTest {

    private static final String BASE_URL = "https://dummy.restapiexample.com/api/v1";
    private static final String EMPLOYEE_LIST_ENDPOINT = "/employee";
    private static final String EMPLOYEE_BY_ID_ENDPOINT = "/employee/{id}";

    private final List<Employee> employeeList = new ArrayList<>();

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EmployeeApiProperties apiProperties;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    public void setup() {
        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getAllEmployeeEndpoint()).thenReturn(EMPLOYEE_LIST_ENDPOINT);
        when(apiProperties.getEmployeeByIdEndpoint()).thenReturn(EMPLOYEE_BY_ID_ENDPOINT);

        employeeList.add(Employee.builder()
                .id(UUID.randomUUID())
                .name("Ranjit")
                .age(23)
                .salary(1000)
                .build());
        // Add more mock employees as needed
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
        List<Employee> filtered = employeeService.getEmployeesByNameSearch("Ran");
        assertEquals(filtered.get(0).getName(), "Ranjit");
    }

    @Test
    public void testGetEmployeeById_Success() throws URISyntaxException {
        // Arrange
        String emp_id = "87986a7e-4463-4dab-8eaa-a24089673ed1";

        Employee expectedEmployee = Employee.builder()
                .id(UUID.fromString(emp_id))
                .name("Ranjit")
                .salary(1000)
                .age(30)
                .title("Manager")
                .email("ranjit@example.com")
                .build();

        // Set up mock configuration properties
        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getEmployeeByIdEndpoint()).thenReturn(EMPLOYEE_BY_ID_ENDPOINT);

        URI resolvedUri = new URI(BASE_URL + EMPLOYEE_BY_ID_ENDPOINT.replace("{id}", emp_id));

        // Create mock API response body
        ClientResponse<Employee> mockClientResponse = new ClientResponse<>();
        mockClientResponse.setData(expectedEmployee);

        ResponseEntity<ClientResponse<Employee>> mockResponse =
                new ResponseEntity<>(mockClientResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(resolvedUri),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        // Act
        Employee actualEmployee = employeeService.getEmployeeById(emp_id);

        // Assert
        assertNotNull(actualEmployee);
        assertEquals(expectedEmployee.getName(), actualEmployee.getName());
        assertEquals(expectedEmployee.getSalary(), actualEmployee.getSalary());
        assertEquals(expectedEmployee.getId(), actualEmployee.getId());
    }



    private void getAllEmployee() throws URISyntaxException {
        EmployeeList employeeListWrapper = new EmployeeList(this.employeeList);

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getAllEmployeeEndpoint()).thenReturn(EMPLOYEE_LIST_ENDPOINT);

        URI uri = new URI(BASE_URL+EMPLOYEE_LIST_ENDPOINT);

        when(restTemplate.exchange(
                eq(uri),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeList.class)))
                .thenReturn(new ResponseEntity<>(employeeListWrapper, HttpStatus.OK));
    }

}
