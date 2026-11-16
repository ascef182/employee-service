package com.example.employee_service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Employees {

    private List<Employee> employeeList = new ArrayList<>();

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public Optional<Employee> findById(String employeeId) {
        return employeeList.stream()
                .filter(e -> e.getEmployeeId().equals(employeeId))
                .findFirst();
    }
}
