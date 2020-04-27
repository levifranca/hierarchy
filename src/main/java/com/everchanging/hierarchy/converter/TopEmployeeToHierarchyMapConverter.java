package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class TopEmployeeToHierarchyMapConverter implements Converter<Employee, Map<String, Object>> {
    private static final Object EMPTY_OBJECT = new Object();

    @Override
    public Map<String, Object> convert(@NotNull Employee topLevelEmployee) {
        log.debug("Convert from top topLevelEmployee {} to hierarchy map", topLevelEmployee);
        if (topLevelEmployee.getSupervisor() != null) {
            log.error("Received a non-top topLevelEmployee to convert into hierarchy map. {}", topLevelEmployee);
            throw new IllegalStateException();
        }

        return Map.of(topLevelEmployee.getName(), convertList(topLevelEmployee.getSubordinates()));
    }

    public Object convertList(Set<Employee> employees) {
        if (employees.isEmpty()) {
            return EMPTY_OBJECT;
        }
        return employees.stream()
                .collect(toMap(Employee::getName, employee -> convertList(employee.getSubordinates())));
    }
}
