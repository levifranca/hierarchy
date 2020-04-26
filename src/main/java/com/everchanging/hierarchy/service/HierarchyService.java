package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class HierarchyService {

    public Employee computeHierarchy(Map<String, String> hierarchyRequest) {
        Map<String, Employee> employeesMap = new HashMap<>();

        for (Map.Entry<String, String> entry : hierarchyRequest.entrySet()) {
            String subordinateName = entry.getKey();
            String supervisorName = entry.getValue();

            Employee supervisor = employeesMap.computeIfAbsent(supervisorName, Employee::new);
            Employee subordinate = employeesMap.computeIfAbsent(subordinateName, Employee::new);

            supervisor.addSubordinate(subordinate);
            subordinate.setSupervisor(supervisor);
        }

        return employeesMap.values().stream()
                .filter(employee -> Objects.isNull(employee.getSupervisor()))
                .findFirst().orElse(null);
    }
}
