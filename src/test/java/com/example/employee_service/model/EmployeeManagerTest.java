package com.example.employee_service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmployeeManagerTest {

    private final EmployeeManager manager = new EmployeeManager();

    @Test
    void managerStartsWithFourSeededEmployees() {
        assertNotNull(manager.getEmployees());
        assertEquals(4, manager.getEmployees().getEmployeeList().size());
    }

    @Test
    void seededEmployeesCanBeFoundById() {
        Employee ana = manager.getEmployees().findById("001").orElseThrow();

        assertEquals("Ana", ana.getFirstName());
        assertEquals("Silva", ana.getLastName());
    }
}
