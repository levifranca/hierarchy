package com.everchanging.hierarchy.service

import com.everchanging.hierarchy.converter.TopEmployeeToEntityListConverter
import com.everchanging.hierarchy.dto.EmployeeSupervisors
import com.everchanging.hierarchy.exception.EmployeeNotFoundException
import com.everchanging.hierarchy.model.Employee
import com.everchanging.hierarchy.repository.EmployeeRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class EmployeeService(
        private val repository: EmployeeRepository,
        private val converter: TopEmployeeToEntityListConverter
) {

    @Transactional
    open fun refreshHierarchy(topLevelEmployee: Employee) {
        val entities = converter.convert(topLevelEmployee)

        repository.deleteAll()
        repository.saveAll(entities)
    }

    open fun getSupervisors(employeeName: String): EmployeeSupervisors {
        return repository.getSupervisors(employeeName) ?: throw EmployeeNotFoundException(employeeName)
    }
}