package com.everchanging.hierarchy.validator

import com.everchanging.hierarchy.dto.ValidationError
import com.everchanging.hierarchy.model.Employee

interface HierarchyValidator {
    fun validate(employees: Set<Employee>): List<ValidationError>
}
