package com.everchanging.hierarchy.service

import com.everchanging.hierarchy.dto.ValidationError
import com.everchanging.hierarchy.exception.HierarchyValidationException
import com.everchanging.hierarchy.model.Employee
import com.everchanging.hierarchy.validator.HierarchyValidator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HierarchyService(
        private val hierarchyValidators: List<HierarchyValidator>,
        private val employeeService: EmployeeService
) {
    companion object {
        private val log = LoggerFactory.getLogger(HierarchyService::class.java)
    }

    fun computeAndSaveHierarchy(hierarchyRequest: Map<String, String>): Employee {
        val employees = resolveEmployees(hierarchyRequest)

        val validationErrors: List<ValidationError> = hierarchyValidators.asSequence()
                .map { validator -> validator.validate(employees) }
                .flatten()
                .toList()

        if (validationErrors.isNotEmpty()) {
            log.debug("Validation errors $validationErrors found on list of employees $employees")
            throw HierarchyValidationException(validationErrors)
        }
        val topLevelEmployee = getTopLevelEmployee(employees)

        employeeService.refreshHierarchy(topLevelEmployee)

        return topLevelEmployee
    }

    private fun getTopLevelEmployee(employees: Set<Employee>): Employee {
        // After validation it is safe to say there is only one employee without a supervisor
        return employees.asSequence()
                .filter { employee -> employee.supervisor == null }
                .first()
    }

    private fun resolveEmployees(hierarchyRequest: Map<String, String>): Set<Employee> {
        val employeesMap: MutableMap<String, Employee> = mutableMapOf()

        for (entry: Map.Entry<String, String> in hierarchyRequest.entries) {
            val subordinateName = entry.key
            val supervisorName = entry.value

            val subordinate = employeesMap.getOrPut(subordinateName) { Employee(subordinateName) }
            val supervisor = employeesMap.getOrPut(supervisorName) { Employee(supervisorName) }

            supervisor.addSubordinate(subordinate)
        }
        return employeesMap.values.toSet()
    }

}

