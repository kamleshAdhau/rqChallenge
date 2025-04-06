package com.reliaquest.api.controller;

import com.reliaquest.api.ApiApplicationTest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.impl.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class MockEmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private final Employee dummyEmployee = Employee.builder()
            .id("123")
            .employeeName("John Doe")
            .employeeAge(30)
            .employeeSalary(100000)
            .title("Engineer")
            .build();

    @Test
    void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(dummyEmployee));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeName").value("John Doe"));
    }

    @Test
    void testGetEmployeesByNameSearch() throws Exception {
        when(employeeService.getEmployeesByNameSearch("John")).thenReturn(List.of(dummyEmployee));

        mockMvc.perform(get("/search/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeName").value("John Doe"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById("123")).thenReturn(dummyEmployee);

        mockMvc.perform(get("/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeName").value("John Doe"));
    }

    @Test
    void testGetHighestSalaryOfEmployees() throws Exception {
        when(employeeService.getHighestSalaryOfEmployee()).thenReturn(100000);

        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("100000"));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(List.of("John Doe", "Jane Smith"));

        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("John Doe"))
                .andExpect(jsonPath("$[1]").value("Jane Smith"));
    }

    @Test
    void testCreateEmployee() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "John Doe");
        input.put("salary", "100000");
        input.put("age", "30");
        input.put("title", "Engineer");

        when(employeeService.createEmployee(any())).thenReturn(dummyEmployee);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"salary\": \"100000\", \"age\": \"30\", \"title\": \"Engineer\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeName").value("John Doe"));
    }

    @Test
    void testDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployee("123")).thenReturn("Employee deleted");

        mockMvc.perform(delete("/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted"));
    }
}
