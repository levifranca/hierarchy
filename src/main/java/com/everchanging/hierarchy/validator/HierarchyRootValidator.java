package com.everchanging.hierarchy.validator;

import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Component
public class HierarchyRootValidator implements HierarchyValidator {

    @Override
    public List<ValidationError> validate(Set<Employee> employees) {
        if (employees.isEmpty()) {
            log.debug("Empty collection of employees passed to check root.");
            return Collections.emptyList();
        }

        List<Employee> rootEmployees = employees.stream()
                .filter(employee -> employee.getSupervisor() == null)
                .collect(Collectors.toList());

        if (rootEmployees.size() == 1) {
            log.trace("Found only one employee without supervisor. No issue with hierarchy roots.");
            return Collections.emptyList();
        }

        if (rootEmployees.isEmpty()) {
            log.trace("No employee without supervisor found.");
            return List.of(new ValidationError("HierarchyInvalidRoot", "No hierarchy root found."));
        }

        log.trace("Multiple roots found on employee's hierarchy");
        List<String> rootEmployeeNames = rootEmployees.stream().map(Employee::getName).sorted().collect(Collectors.toList());
        String message = format("Found %d roots in the hierarchy. Roots employees are %s", rootEmployees.size(), rootEmployeeNames);
        return List.of(new ValidationError("HierarchyInvalidRoot", message));
    }
}
