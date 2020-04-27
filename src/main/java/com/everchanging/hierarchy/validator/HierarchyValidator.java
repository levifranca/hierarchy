package com.everchanging.hierarchy.validator;

import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.model.Employee;

import java.util.List;
import java.util.Set;

public interface HierarchyValidator {
    List<ValidationError> validate(Set<Employee> employees);
}
