package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.model.Employee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Component
public class EmployeeToHierarchyMapConverter implements Converter<Employee, Map<String, Object>> {
    private static final Object EMPTY_OBJECT = new Object();

    /**
     * @param employee The root employee in the hierarchy (top level)
     * @return A Map containing the Employee's hierarchy
     * @throws StackOverflowError in case there are loops in the hierarchy
     * (e.g. employee A supervises employee B and B supervises A.)
     */
    @Override
    public Map<String, Object> convert(@NotNull Employee employee) {
        return Map.of(employee.getName(), convertList(employee.getSubordinates()));
    }

    public Object convertList(Set<Employee> employees) {
        if (employees.isEmpty()) {
            return EMPTY_OBJECT;
        }
        return employees.stream()
                .collect(toMap(Employee::getName, employee -> convertList(employee.getSubordinates())));
    }
}
