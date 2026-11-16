package com.example.employee_service.controller;

import com.example.employee_service.model.Employee;
import com.example.employee_service.model.EmployeeManager;
import com.example.employee_service.model.Employees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeManager employeeManager;

    private Employees employees;

    @BeforeEach
    void setUp() {
        employees = new Employees();
        employees.addEmployee(new Employee("001", "Ana", "Silva", "ana@empresa.com", "Developer"));
        employees.addEmployee(new Employee("002", "Bruno", "Costa", "bruno@empresa.com", "Designer"));
        when(employeeManager.getEmployees()).thenReturn(employees);
    }

    @Test
    void getAllReturnsAllEmployees() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].employee_id", is("001")))
                .andExpect(jsonPath("$[0].first_name", is("Ana")));
    }

    @Test
    void getByIdReturnsTheEmployee() throws Exception {
        mockMvc.perform(get("/employees/002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", is("Bruno")));
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        mockMvc.perform(get("/employees/999"))
                .andExpect(status().isNotFound());
    }

        @Test
    void postCreatesEmployeeAndReturnsLocation() throws Exception {
        String body = """
                {"employee_id":"010","first_name":"Eva","last_name":"Reis",
                 "email":"eva@empresa.com","title":"DevOps"}
                """;

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/employees/010"))
                .andExpect(jsonPath("$.employee_id", is("010")))
                .andExpect(jsonPath("$.first_name", is("Eva")))
                .andExpect(jsonPath("$.last_name", is("Reis")))
                .andExpect(jsonPath("$.email", is("eva@empresa.com")))
                .andExpect(jsonPath("$.title", is("DevOps")));

        assertEquals(3, employees.getEmployeeList().size());

        Employee saved = employees.findById("010").orElseThrow();
        assertEquals("010", saved.getEmployeeId());
        assertEquals("Eva", saved.getFirstName());
        assertEquals("Reis", saved.getLastName());
        assertEquals("eva@empresa.com", saved.getEmail());
        assertEquals("DevOps", saved.getTitle());
    }

    @Test
    void postRejectsNonJsonContent() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not json"))
                .andExpect(status().isUnsupportedMediaType());
    }
        @Test
    void postRejectsDuplicateEmployeeId() throws Exception {
        String body = """
                {"employee_id":"001","first_name":"Eva","last_name":"Reis",
                 "email":"eva@empresa.com","title":"DevOps"}
                """;

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        assertEquals(2, employees.getEmployeeList().size());
    }

    @Test
    void postRejectsMissingEmployeeId() throws Exception {
        String body = """
                {"first_name":"Eva","last_name":"Reis",
                 "email":"eva@empresa.com","title":"DevOps"}
                """;

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        assertEquals(2, employees.getEmployeeList().size());
    }

}
