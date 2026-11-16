package com.example.employee_service.controller;

import com.example.employee_service.model.Employee;
import com.example.employee_service.model.EmployeeManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.employee_service.model.Employees;
import org.springframework.http.HttpStatus;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeManager employeeManager;

    public EmployeeController(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeManager.getEmployees().getEmployeeList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable String id) {
        return employeeManager.getEmployees().findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

      @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        String id = employee.getEmployeeId();
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Employees employees = employeeManager.getEmployees();
        if (employees.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        employees.addEmployee(employee);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(employee);
    }

}
