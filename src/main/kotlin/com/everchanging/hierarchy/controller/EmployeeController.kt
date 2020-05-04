package com.everchanging.hierarchy.controller

import com.everchanging.hierarchy.dto.EmployeeSupervisors
import com.everchanging.hierarchy.service.EmployeeService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class EmployeeController(private val employeeService: EmployeeService) {

    @GetMapping(
            path = ["/employees/{name}/supervisors"],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSupervisors(@PathVariable("name") employeeName: String): EmployeeSupervisors {
        return employeeService.getSupervisors(employeeName)
    }
}