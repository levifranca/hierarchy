package com.everchanging.hierarchy.validator

import com.everchanging.hierarchy.converter.NamesLoopToValidationErrorConverter
import com.everchanging.hierarchy.dto.ValidationError
import com.everchanging.hierarchy.model.Employee
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class HierarchyLoopValidator(val converter: NamesLoopToValidationErrorConverter) : HierarchyValidator {
    companion object {
        private val log = LoggerFactory.getLogger(HierarchyLoopValidator::class.java)
    }

    override fun validate(employees: Set<Employee>): List<ValidationError> {
        if (employees.isEmpty()) {
            log.debug("Empty set of employees has no loop")
            return emptyList()
        }

        val visited: MutableSet<Employee> = mutableSetOf()
        val nameLoops: MutableList<ArrayList<String>> = mutableListOf()
        for (employee in employees) {
            val nameLoop = traverse(employee, visited, mutableSetOf())
            if (nameLoop.isNotEmpty()) {
                log.debug("Found a loop. Names on it are: $nameLoop")
                nameLoops.add(nameLoop)
            }
        }

        log.debug("Converting loops found $nameLoops to validation errors")
        return nameLoops.map { converter.convert(it) }.toList()
    }

    private fun traverse(employee: Employee, visited: MutableSet<Employee>, visitedRec: MutableSet<Employee>): ArrayList<String> {
        if (visitedRec.contains(employee)) {
            log.trace("Found a loop. Employee $employee is has been already visited in this recursion.")
            return arrayListOf(employee.name)
        }
        if (visited.contains(employee)) {
            log.trace("Employee $employee has already been visited before. Stop Recursion.")
            return arrayListOf()
        }

        visited.add(employee)
        visitedRec.add(employee)

        val supervisor = employee.supervisor
        if (supervisor == null) {
            log.trace("Employee $employee has no supervisor. No possible loop found.")
            return arrayListOf()
        }

        val nameLoop = traverse(supervisor, visited, visitedRec)
        if (nameLoop.isNotEmpty()) {
            log.trace("Loop found on $employee hierarchy. Adding it to the names in the loop")
            nameLoop.add(employee.name)
            return nameLoop
        }

        return arrayListOf()
    }
}