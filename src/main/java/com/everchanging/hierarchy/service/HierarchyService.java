package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.converter.EmployeeToHierarchyMapConverter;
import com.everchanging.hierarchy.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class HierarchyService {

    private final EmployeeToHierarchyMapConverter employeeToHierarchyMapConverter;

    public Map<String, Object> createHierarchy(Map<String, String> hierarchyRequest) {
        Map<String, Employee> employeesMap = new HashMap<>();

        for (Map.Entry<String, String> entry : hierarchyRequest.entrySet()) {
            String subordinateName = entry.getKey();
            String supervisorName = entry.getValue();

            Employee supervisor = employeesMap.computeIfAbsent(supervisorName, Employee::new);
            Employee subordinate = employeesMap.computeIfAbsent(subordinateName, Employee::new);

            supervisor.addSubordinate(subordinate);
            subordinate.setSupervisor(supervisor);
        }

        Employee rootEmployee = employeesMap.values().stream()
                .filter(employee -> Objects.isNull(employee.getSupervisor()))
                .findFirst().orElse(null);

        return employeeToHierarchyMapConverter.convert(rootEmployee);
    }
}
