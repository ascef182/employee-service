package com.example.employee_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeesTest {

    private Employees employees;
    private Employee ana;

    @BeforeEach
    void setUp() {
        employees = new Employees();
        ana = new Employee("001", "Ana", "Silva", "ana@empresa.com", "Developer");
    }

    @Test
    void newCollectionIsEmpty() {
        assertTrue(employees.getEmployeeList().isEmpty());
    }

    @Test
    void addEmployeeStoresTheEmployee() {
        employees.addEmployee(ana);

        assertEquals(1, employees.getEmployeeList().size());
        assertSame(ana, employees.getEmployeeList().get(0));
    }

    @Test
    void findByIdReturnsTheMatchingEmployee() {
        employees.addEmployee(ana);

        assertSame(ana, employees.findById("001").orElseThrow());
    }

    @Test
    void findByIdReturnsEmptyWhenIdDoesNotExist() {
        employees.addEmployee(ana);

        assertTrue(employees.findById("999").isEmpty());
    }

    @Test
    void setEmployeeListReplacesTheContent() {
        employees.addEmployee(ana);

        employees.setEmployeeList(List.of());

        assertTrue(employees.getEmployeeList().isEmpty());
    }
}
