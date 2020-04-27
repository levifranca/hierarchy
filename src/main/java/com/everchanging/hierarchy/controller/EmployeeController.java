package com.everchanging.hierarchy.controller;

import com.everchanging.hierarchy.dto.EmployeeSupervisors;
import com.everchanging.hierarchy.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping(
            path = "/employees/{name}/supervisors",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeSupervisors getSupervisors(@PathVariable("name") String employeeName) {
        return employeeService.getSupervisors(employeeName);
    }
}
