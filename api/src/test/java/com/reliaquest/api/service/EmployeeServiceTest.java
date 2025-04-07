package com.reliaquest.api.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.*;
import com.reliaquest.api.service.impl.EmployeeService;
import com.reliaquest.api.utils.Constants;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
                .id(UUID.fromString("8f64b0d7-c2bb-4bfa-bcd7-81ec33b71d2a"))
                .name("Ranjit")
                .age(23)
                .salary(1000)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("8f64b0d7-c2bb-4bfa-bcd7-81ec33b71d2a"))
                .name("Bradley")
                .age(25)
                .salary(1002)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("e574df17-75a3-432f-9016-dc4a89c0edc0"))
                .name("Tiger")
                .age(26)
                .salary(1004)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("9c4cfc6e-e4a3-40e8-9f2b-786ba28e8db0"))
                .name("Nixon")
                .age(27)
                .salary(100001)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("82a3027a-81f3-4437-a2aa-f79d61a939f1"))
                .name("Kennedy")
                .age(27)
                .salary(100006)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("fcb875fd-dcd9-4976-8e8c-96d77e8dd94d"))
                .name("Haley")
                .age(27)
                .salary(10000)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("c361e183-7c2a-4f24-a99e-2750c8e6f4e1"))
                .name("Doris")
                .age(27)
                .salary(10001)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("43822a99-5e87-4876-a9fe-6b7dd4fcb6d6"))
                .name("Vance")
                .age(27)
                .salary(10002)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("7a99b45a-c9c7-4144-a215-7e2741e262a2"))
                .name("Caesar")
                .age(27)
                .salary(10003)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("a19cd15b-55f5-4e35-b2e8-55ab9a2a6a18"))
                .name("Yuri")
                .age(27)
                .salary(10004)
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("f2a6e2fc-5a26-4efb-8047-7f65ebc5db2f"))
                .name("Jenette")
                .age(27)
                .salary(10005)
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

        assertEquals(allEmployeesList.get(0).getName(), "Ranjit");
    }

    @Test
    public void testGetEmployeeById_Success() {
        String id = "87986a7e-4463-4dab-8eaa-a24089673ed1";
        UUID uuid = UUID.fromString(id);

        Employee employee = Employee.builder()
                .id(uuid)
                .name("Ranjit")
                .salary(1000)
                .age(30)
                .title("Manager")
                .email("ranjit@example.com")
                .build();

        ClientResponse<Employee> clientResponse = new ClientResponse<>();
        clientResponse.setData(employee);

        ResponseEntity<ClientResponse<Employee>> responseEntity = new ResponseEntity<>(clientResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq(Constants.GET_EMPLOYEE_ID_URL),
                        eq(HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class),
                        eq(id)))
                .thenReturn(responseEntity);

        Employee result = employeeService.getEmployeeById(id);
        assertNotNull(result);
        assertEquals("Ranjit", result.getName());
    }

    @Test
    public void testGetEmployeeById_TooManyRequests() {
        String id = "rate-limited-id";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", "30");

        HttpClientErrorException tooManyRequests =
                HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", headers, null, null);

        when(restTemplate.exchange(
                        eq(Constants.GET_EMPLOYEE_ID_URL),
                        eq(HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class),
                        eq(id)))
                .thenThrow(tooManyRequests);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> employeeService.getEmployeeById(id));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        String id = "non-existent-id";

        // Simulate 404 error
        HttpClientErrorException notFoundException =
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null);

        when(restTemplate.exchange(
                        eq(Constants.GET_EMPLOYEE_ID_URL),
                        eq(HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class),
                        eq(id)))
                .thenThrow(notFoundException);

        Employee result = employeeService.getEmployeeById(id);

        // Since exception is caught and handled as returning null
        assertNull(result);
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
    void testCreateEmployee() {
        // Arrange
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", "John Doe");
        employeeInput.put("salary", "50000");
        employeeInput.put("age", "30");
        employeeInput.put("title", "Engineer");

        UUID uuid = UUID.randomUUID();
        Employee expectedEmployee = Employee.builder()
                .id(uuid)
                .name("John Doe")
                .salary(50000)
                .age(30)
                .title("Engineer")
                .build();

        EmployeeResponse mockResponse = EmployeeResponse.builder()
                .data(expectedEmployee)
                .status("Successfully processed request.")
                .build();

        ResponseEntity<EmployeeResponse> mockEntity = new ResponseEntity<>(mockResponse, HttpStatus.CREATED);

        when(restTemplate.exchange(
                        eq(Constants.GET_EMPLOYEE_URL),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        eq(EmployeeResponse.class)))
                .thenReturn(mockEntity);

        // Act
        Employee actualEmployee = employeeService.createEmployee(employeeInput);

        // Assert
        assertEquals(expectedEmployee, actualEmployee);
        verify(restTemplate, times(1))
                .exchange(
                        eq(Constants.GET_EMPLOYEE_URL),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        eq(EmployeeResponse.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteEmployee_Success() {
        // UUID and ID
        UUID employeeUUID = UUID.fromString("87986a7e-4463-4dab-8eaa-a24089673ed1");
        String id = employeeUUID.toString();

        // Setup employee to be returned by GET
        Employee employee = Employee.builder()
                .id(employeeUUID)
                .name("Ranjit")
                .salary(1000)
                .age(30)
                .title("Manager")
                .email("ranjit@example.com")
                .build();

        ClientResponse<Employee> getClientResponse = new ClientResponse<>();
        getClientResponse.setData(employee);

        ResponseEntity<ClientResponse<Employee>> getResponse = new ResponseEntity<>(getClientResponse, HttpStatus.OK);

        // Setup DELETE response
        DeleteEmployeeResponse<Boolean> deleteResponse = new DeleteEmployeeResponse<>();
        deleteResponse.setData(true);
        deleteResponse.setStatus("Success");

        ResponseEntity<DeleteEmployeeResponse<Boolean>> deleteResponseEntity =
                new ResponseEntity<>(deleteResponse, HttpStatus.OK);

        // Mock GET employee by ID
        when(restTemplate.exchange(
                        eq(Constants.GET_EMPLOYEE_ID_URL),
                        eq(HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class),
                        eq(id)))
                .thenReturn(getResponse);

        // Mock DELETE employee
        when(restTemplate.exchange(
                        eq(Constants.GET_EMPLOYEE_URL),
                        eq(HttpMethod.DELETE),
                        any(HttpEntity.class),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(deleteResponseEntity);

        // 🔥 Execute service method
        String result = employeeService.deleteEmployee(id);

        // ✅ Validate
        assertEquals("Employee Ranjit has been deleted", result);
    }

    private void getAllEmployee() throws URISyntaxException {
        EmployeeList employeeList = new EmployeeList(this.employeeList);
        when(restTemplate.exchange(new URI(Constants.GET_EMPLOYEE_URL), HttpMethod.GET, null, EmployeeList.class))
                .thenReturn(new ResponseEntity<>(employeeList, HttpStatus.OK));
    }
}
