package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.exception.HierarchyValidationException;
import com.everchanging.hierarchy.model.Employee;
import com.everchanging.hierarchy.validator.HierarchyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class HierarchyService {

    private final HierarchyValidator hierarchyValidator;

    public Employee computeHierarchy(Map<String, String> hierarchyRequest) {
        Set<Employee> employees = resolveEmployees(hierarchyRequest);

        List<ValidationError> validationErrors = hierarchyValidator.validate(employees);

        if (!validationErrors.isEmpty()) {
            log.debug("Validation errors {} found on list of employees {}", validationErrors, employees);
            throw new HierarchyValidationException(validationErrors);
        }

        return employees.stream()
                .filter(employee -> Objects.isNull(employee.getSupervisor()))
                .findFirst().orElse(null);
    }

    private Set<Employee> resolveEmployees(Map<String, String> hierarchyRequest) {
        Map<String, Employee> employeesMap = new HashMap<>();

        for (Map.Entry<String, String> entry : hierarchyRequest.entrySet()) {
            String subordinateName = entry.getKey();
            String supervisorName = entry.getValue();

            Employee subordinate = employeesMap.computeIfAbsent(subordinateName, Employee::new);
            Employee supervisor = employeesMap.computeIfAbsent(supervisorName, Employee::new);

            supervisor.addSubordinate(subordinate);
        }
        return Set.of(employeesMap.values().toArray(new Employee[0]));
    }
}
