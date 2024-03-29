package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.exception.HierarchyValidationException;
import com.everchanging.hierarchy.model.Employee;
import com.everchanging.hierarchy.validator.HierarchyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Service
public class HierarchyService {

    private final List<HierarchyValidator> hierarchyValidators;
    private final EmployeeService employeeService;

    public Employee computeAndSaveHierarchy(Map<String, String> hierarchyRequest) {
        Set<Employee> employees = resolveEmployees(hierarchyRequest);

        List<ValidationError> validationErrors = hierarchyValidators.stream()
                .map(validator -> validator.validate(employees))
                .flatMap(List::stream).collect(toList());

        if (!validationErrors.isEmpty()) {
            log.debug("Validation errors {} found on list of employees {}", validationErrors, employees);
            throw new HierarchyValidationException(validationErrors);
        }
        Employee topLevelEmployee = getTopLevelEmployee(employees);

        employeeService.refreshHierarchy(topLevelEmployee);

        return topLevelEmployee;
    }

    private Employee getTopLevelEmployee(Set<Employee> employees) {
        // After validation it is safe to say there is only one employee without a supervisor
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
