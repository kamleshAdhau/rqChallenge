package com.reliaquest.api.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeList;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import com.reliaquest.api.utils.EmployeeApiProperties;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
                .id(UUID.fromString("8f64b0d7-c2bb-4bfa-bcd7-81ec33b71a6a"))
                .name("Ranjit")
                .age(23)
                .salary(1000)
                .email("ranjit@example.com")
                .title("Developer")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("8f64b123-c2bb-4bfa-bcd7-81ec33b71b2a"))
                .name("Ranjeet")
                .age(23)
                .salary(1000)
                .email("ranjeet@example.com")
                .title("Tester")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("8f64b0d7-c2bb-4bfa-bcd7-81ec33b71c3a"))
                .name("Bradley")
                .age(25)
                .salary(1002)
                .email("bradley@example.com")
                .title("Engineer")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("e574df17-75a3-432f-9016-dc4a89c0edc0"))
                .name("Tiger")
                .age(26)
                .salary(1004)
                .email("tiger@example.com")
                .title("Architect")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("9c4cfc6e-e4a3-40e8-9f2b-786ba28e8db0"))
                .name("Nixon")
                .age(27)
                .salary(100001)
                .email("nixon@example.com")
                .title("VP Engineering")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("82a3027a-81f3-4437-a2aa-f79d61a939f1"))
                .name("Kennedy")
                .age(27)
                .salary(100006)
                .email("kennedy@example.com")
                .title("CTO")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("fcb875fd-dcd9-4976-8e8c-96d77e8dd94d"))
                .name("Haley")
                .age(27)
                .salary(10000)
                .email("haley@example.com")
                .title("Manager")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("c361e183-7c2a-4f24-a99e-2750c8e6f4e1"))
                .name("Doris")
                .age(27)
                .salary(10001)
                .email("doris@example.com")
                .title("Lead QA")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("43822a99-5e87-4876-a9fe-6b7dd4fcb6d6"))
                .name("Vance")
                .age(27)
                .salary(10002)
                .email("vance@example.com")
                .title("Sr. Developer")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("7a99b45a-c9c7-4144-a215-7e2741e262a2"))
                .name("Caesar")
                .age(27)
                .salary(10003)
                .email("caesar@example.com")
                .title("Product Owner")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("a19cd15b-55f5-4e35-b2e8-55ab9a2a6a18"))
                .name("Yuri")
                .age(27)
                .salary(10004)
                .email("yuri@example.com")
                .title("Team Lead")
                .build());
        employeeList.add(Employee.builder()
                .id(UUID.fromString("f2a6e2fc-5a26-4efb-8047-7f65ebc5db2f"))
                .name("Jenette")
                .age(27)
                .salary(10005)
                .email("jenette@example.com")
                .title("Director")
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
    void testGetAllEmployees_whenResponseBodyIsNull_shouldThrowException() throws URISyntaxException {
        URI uri = new URI(BASE_URL + EMPLOYEE_LIST_ENDPOINT);

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getAllEmployeeEndpoint()).thenReturn(EMPLOYEE_LIST_ENDPOINT);

        ResponseEntity<EmployeeList> response = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), isNull(), eq(EmployeeList.class)))
                .thenReturn(response);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> employeeService.getAllEmployees());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("External service returned empty response", exception.getReason());
    }

    @Test
    public void testGetEmployeesByNameSearch() throws URISyntaxException, IOException {
        getAllEmployee();
        List<Employee> filtered = employeeService.getEmployeesByNameSearch("Ran");
        assertEquals(2, filtered.size());
        assertEquals(filtered.get(0).getName(), "Ranjit");
    }

    @Test
    public void testGetEmployeeById_Success() throws URISyntaxException {
        String empId = "8f64b0d7-c2bb-4bfa-bcd7-81ec33b71a6a";

        Employee expectedEmployee = Employee.builder()
                .id(UUID.fromString(empId))
                .name("Ranjit")
                .salary(1000)
                .age(30)
                .title("Manager")
                .email("ranjit@example.com")
                .build();

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getEmployeeByIdEndpoint()).thenReturn(EMPLOYEE_BY_ID_ENDPOINT);

        URI resolvedUri = new URI(BASE_URL + EMPLOYEE_BY_ID_ENDPOINT.replace("{id}", empId));

        ApiResponse<Employee> mockApiResponse = ApiResponse.<Employee>builder()
                .data(expectedEmployee)
                .status("success")
                .build();

        ResponseEntity<ApiResponse<Employee>> mockResponse = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq(resolvedUri), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        Employee actualEmployee = employeeService.getEmployeeById(empId);

        assertNotNull(actualEmployee);
        assertEquals(expectedEmployee.getName(), actualEmployee.getName());
        assertEquals(expectedEmployee.getSalary(), actualEmployee.getSalary());
        assertEquals(expectedEmployee.getId(), actualEmployee.getId());
    }

    @Test
    void testGetEmployeeById_whenDataIsNull_shouldThrowException() throws URISyntaxException {
        String empId = "8f64b0d7-c2bb-4bfa-bcd7-81ec33b71a6a";
        URI uri = new URI(BASE_URL + EMPLOYEE_BY_ID_ENDPOINT.replace("{id}", empId));

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getEmployeeByIdEndpoint()).thenReturn(EMPLOYEE_BY_ID_ENDPOINT);

        ApiResponse<Employee> apiResponse = new ApiResponse<>();
        apiResponse.setData(null);

        ResponseEntity<ApiResponse<Employee>> response = new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> employeeService.getEmployeeById(empId));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("External service returned empty response", exception.getReason());
    }

    private void getAllEmployee() throws URISyntaxException {
        EmployeeList employeeListWrapper = new EmployeeList(this.employeeList);

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getAllEmployeeEndpoint()).thenReturn(EMPLOYEE_LIST_ENDPOINT);

        URI uri = new URI(BASE_URL + EMPLOYEE_LIST_ENDPOINT);

        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), isNull(), eq(EmployeeList.class)))
                .thenReturn(new ResponseEntity<>(employeeListWrapper, HttpStatus.OK));
    }

    @Test
    void testCreateEmployee_responseBodyIsNull_shouldThrowException() {
        Map<String, Object> employeeInput = Map.of(
                "name", "John",
                "age", "25",
                "salary", "50000",
                "title", "Engineer");

        URI uri = URI.create(BASE_URL + EMPLOYEE_LIST_ENDPOINT);

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getAllEmployeeEndpoint()).thenReturn(EMPLOYEE_LIST_ENDPOINT);

        ResponseEntity<ApiResponse<Employee>> response = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                        eq(uri), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        ResponseStatusException thrown =
                assertThrows(ResponseStatusException.class, () -> employeeService.createEmployee(employeeInput));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
        assertTrue(thrown.getReason().contains("External service returned empty response"));
    }

    @Test
    void testGetHighestSalaryOfEmployee_shouldReturnMaxSalary() throws URISyntaxException {
        getAllEmployee();
        Integer maxSalary = employeeService.getHighestSalaryOfEmployee();
        assertEquals(Integer.valueOf(100006), maxSalary); // based on setup data
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_shouldReturnCorrectList() throws URISyntaxException {
        getAllEmployee();
        String[] expectedNames = {
            "Kennedy", "Nixon", "Jenette", "Yuri", "Caesar", "Vance", "Doris", "Haley", "Tiger", "Bradley"
        };
        List<String> expectedNameList = Arrays.asList(expectedNames);
        List<String> actualNamesList = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(10, actualNamesList.size());
        assertEquals(expectedNameList, actualNamesList);
    }

    @Test
    void testCreateEmployee_success_shouldReturnCreatedEmployee() {
        Map<String, Object> employeeInput = Map.of(
                "name", "John",
                "age", "25",
                "salary", "50000",
                "title", "Engineer");

        Employee expected = Employee.builder()
                .id(UUID.randomUUID())
                .name("John")
                .age(25)
                .salary(50000)
                .title("Engineer")
                .build();

        ApiResponse<Employee> responseBody =
                ApiResponse.<Employee>builder().data(expected).status("success").build();

        URI uri = URI.create(BASE_URL + EMPLOYEE_LIST_ENDPOINT);
        ResponseEntity<ApiResponse<Employee>> response = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getAllEmployeeEndpoint()).thenReturn(EMPLOYEE_LIST_ENDPOINT);
        when(restTemplate.exchange(
                        eq(uri), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        Employee created = employeeService.createEmployee(employeeInput);
        assertNotNull(created);
        assertEquals(expected.getName(), created.getName());
        assertEquals(expected.getSalary(), created.getSalary());
    }

    @Test
    void testDeleteEmployee_success_shouldReturnSuccessMessage() {
        String empId = UUID.randomUUID().toString();
        Employee mockEmp = Employee.builder()
                .id(UUID.fromString(empId))
                .name("DeleteMe")
                .age(30)
                .salary(1000)
                .build();

        ApiResponse<Employee> getResponse =
                ApiResponse.<Employee>builder().data(mockEmp).status("ok").build();

        ApiResponse<Boolean> deleteResponse =
                ApiResponse.<Boolean>builder().data(true).status("ok").build();

        URI getUri = URI.create(BASE_URL + EMPLOYEE_BY_ID_ENDPOINT.replace("{id}", empId));
        URI deleteUri = URI.create(BASE_URL + EMPLOYEE_LIST_ENDPOINT);

        when(apiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(apiProperties.getEmployeeByIdEndpoint()).thenReturn(EMPLOYEE_BY_ID_ENDPOINT);
        when(apiProperties.getAllEmployeeEndpoint()).thenReturn(EMPLOYEE_LIST_ENDPOINT);

        when(restTemplate.exchange(eq(getUri), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(getResponse, HttpStatus.OK));

        when(restTemplate.exchange(
                        eq(deleteUri),
                        eq(HttpMethod.DELETE),
                        any(HttpEntity.class),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(deleteResponse, HttpStatus.OK));

        String result = employeeService.deleteEmployee(empId);
        assertEquals("Employee DeleteMe has been deleted", result);
    }
}
