package com.everchanging.hierarchy.validator;

import com.everchanging.hierarchy.converter.NamesLoopToValidationErrorConverter;
import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Component
public class HierarchyValidator {

    private final NamesLoopToValidationErrorConverter converter;

    public List<ValidationError> validate(Set<Employee> employees) {
        if (employees.isEmpty()) {
            log.debug("Empty set of employees has no loop");
            return Collections.emptyList();
        }

        Set<Employee> visited = new HashSet<>();
        List<ArrayList<String>> nameLoops = new ArrayList<>();
        for(Employee employee : employees) {
            ArrayList<String> nameLoop = traverse(employee, visited, new HashSet<>());
            if (!nameLoop.isEmpty()) {
                log.debug("Found a loop. Names on it are: {}", nameLoop);
                nameLoops.add(nameLoop);
            }
        }
        log.debug("Converting loops found {} to validation errors", nameLoops);
        return nameLoops.stream()
                .map(converter::convert)
                .collect(toList());
    }

    private ArrayList<String> traverse(Employee employee, Set<Employee> visited, Set<Employee> visitedRec) {
        if (visitedRec.contains(employee)) {
            log.trace("Found a loop. Employee {} is has been already visited in this recursion.", employee);
            return new ArrayList<>(Collections.singleton(employee.getName()));
        }
        if (visited.contains(employee)) {
            log.trace("Employee {} has already been visited before. Stop Recursion.", employee);
            return new ArrayList<>();
        }
        visited.add(employee);
        visitedRec.add(employee);

        Employee supervisor = employee.getSupervisor();
        if (supervisor == null) {
            log.trace("Employee {} has no supervisor. No possible loop found.", employee);
            return new ArrayList<>();
        }

        ArrayList<String> nameLoop = traverse(supervisor, visited, visitedRec);
        if (!nameLoop.isEmpty()) {
            log.trace("Loop found on {} hierarchy. Adding it to the names in the loop", employee);
            nameLoop.add(employee.getName());
            return nameLoop;
        }

        return new ArrayList<>();
    }
}
