package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Load only the EmployeeController class
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetEmployeeById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Employee employee = Employee.builder()
                .id(id)
                .name("Alice")
                .salary(5000)
                .age(28)
                .title("Engineer")
                .email("alice@example.com")
                .build();

        Mockito.when(employeeService.getEmployeeById(anyString())).thenReturn(employee);

        mockMvc.perform(get("/" + id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("Alice"))
                .andExpect(jsonPath("$.employee_salary").value(5000))
                .andExpect(jsonPath("$.employee_age").value(28))
                .andExpect(jsonPath("$.employee_title").value("Engineer"))
                .andExpect(jsonPath("$.employee_email").value("alice@example.com"));
    }

    @Test
    public void testGetEmployeeById_NotFound() throws Exception {
        Mockito.when(employeeService.getEmployeeById(anyString())).thenReturn(null);

        mockMvc.perform(get("/non-existent-id"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testCreateEmployee_Success() throws Exception {
        Employee created = Employee.builder()
                .id(UUID.randomUUID())
                .name("Charlie")
                .salary(5500)
                .age(30)
                .title("Developer")
                .email("charlie@example.com")
                .build();

        Map<String, Object> requestMap = Map.of(
                "name", "Charlie",
                "salary", "5500",
                "age", "30",
                "title", "Developer");

        Mockito.when(employeeService.createEmployee(Mockito.anyMap())).thenReturn(created);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestMap)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employee_name").value("Charlie"))
                .andExpect(jsonPath("$.employee_salary").value(5500))
                .andExpect(jsonPath("$.employee_age").value(30))
                .andExpect(jsonPath("$.employee_title").value("Developer"))
                .andExpect(jsonPath("$.employee_email").value("charlie@example.com"));
    }

    @Test
    public void testDeleteEmployeeById_Success() throws Exception {
        String uuid = UUID.randomUUID().toString();

        Mockito.when(employeeService.deleteEmployee(uuid)).thenReturn("Employee Alice has been deleted");

        mockMvc.perform(delete("/" + uuid))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee Alice has been deleted"));
    }

    @Test
    public void testGetAllEmployees_Success() throws Exception {
        List<Employee> employees = List.of(
                Employee.builder()
                        .id(UUID.randomUUID())
                        .name("Alice")
                        .salary(5000)
                        .age(28)
                        .title("Engineer")
                        .email("alice@example.com")
                        .build(),
                Employee.builder()
                        .id(UUID.randomUUID())
                        .name("Bob")
                        .salary(6000)
                        .age(35)
                        .title("Manager")
                        .email("bob@example.com")
                        .build());

        Mockito.when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].employee_name").value("Alice"))
                .andExpect(jsonPath("$[1].employee_name").value("Bob"));
    }

    @Test
    public void testGetEmployeesByNameSearch_Success() throws Exception {
        String search = "Ali";

        List<Employee> employees = List.of(Employee.builder()
                .id(UUID.randomUUID())
                .name("Alice")
                .salary(5000)
                .age(28)
                .title("Engineer")
                .email("alice@example.com")
                .build());

        Mockito.when(employeeService.getEmployeesByNameSearch(search)).thenReturn(employees);

        mockMvc.perform(get("/search/" + search))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].employee_name").value("Alice"));
    }

    @Test
    public void testGetHighestSalaryOfEmployees_Success() throws Exception {
        Mockito.when(employeeService.getHighestSalaryOfEmployee()).thenReturn(9000);

        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("9000"));
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames_Success() throws Exception {
        List<String> names = List.of("Alice", "Bob", "Charlie");

        Mockito.when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(names);

        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("Alice"))
                .andExpect(jsonPath("$[1]").value("Bob"))
                .andExpect(jsonPath("$[2]").value("Charlie"));
    }
}
