package com.example.employee_service.model;

import org.springframework.stereotype.Component;

@Component
public class EmployeeManager {

    private final Employees employees = new Employees();

    public EmployeeManager() {
        employees.addEmployee(new Employee("001", "Ana", "Silva", "ana@empresa.com", "Developer"));
        employees.addEmployee(new Employee("002", "Bruno", "Costa", "bruno@empresa.com", "Designer"));
        employees.addEmployee(new Employee("003", "Carla", "Souza", "carla@empresa.com", "Product Manager"));
        employees.addEmployee(new Employee("004", "Diego", "Lima", "diego@empresa.com", "QA Analyst"));
    }

    public Employees getEmployees() {
        return employees;
    }
}
