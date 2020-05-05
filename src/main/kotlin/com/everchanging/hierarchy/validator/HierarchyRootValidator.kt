package com.everchanging.hierarchy.validator

import com.everchanging.hierarchy.dto.ValidationError
import com.everchanging.hierarchy.model.Employee
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class HierarchyRootValidator : HierarchyValidator {
    companion object {
        private val log = LoggerFactory.getLogger(HierarchyRootValidator::class.java)
    }

    override fun validate(employees: Set<Employee>): List<ValidationError> {
        if (employees.isEmpty()) {
            log.debug("Empty collection of employees passed to check root.")
            return emptyList()
        }

        val rootEmployees = employees.asSequence()
                .filter { it.supervisor == null }
                .sortedBy { it.name }
                .toList()

        if (rootEmployees.size == 1) {
            log.trace("Found only one employee without supervisor. No issue with hierarchy roots.")
            return emptyList()
        }

        if (rootEmployees.isEmpty()) {
            log.trace("No employee without supervisor found.")
            return listOf(ValidationError("HierarchyInvalidRoot", "No hierarchy root found."))
        }

        log.trace("Multiple roots found on employee's hierarchy")
        val rootEmployeeNames = rootEmployees.map { it.name }.toList()
        return listOf(ValidationError("HierarchyInvalidRoot",
                "Found ${rootEmployees.size} roots in the hierarchy. Roots employees are $rootEmployeeNames"))
    }

}