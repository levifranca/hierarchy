package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.entity.EmployeeEntity;
import com.everchanging.hierarchy.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class TopEmployeeToEntityListConverter implements Converter<Employee, List<EmployeeEntity>> {
    @Override
    public List<EmployeeEntity> convert(@NotNull Employee topLevelEmployee) {
        log.debug("Convert from top topLevelEmployee {} to list of entities", topLevelEmployee);
        if (topLevelEmployee.getSupervisor() != null) {
            log.error("Received a non-top topLevelEmployee to convert into hierarchy map. {}", topLevelEmployee);
            throw new IllegalStateException();
        }

        List<EmployeeEntity> entities = new ArrayList<>();
        EmployeeEntity employeeEntity = new EmployeeEntity(topLevelEmployee.getName(), null);
        List<EmployeeEntity> subordinateEntities = convertSubordinatesToEntities(topLevelEmployee.getSubordinates(), employeeEntity);

        // Add top-level first then subordinates
        entities.add(employeeEntity);
        entities.addAll(subordinateEntities);

        log.debug("Returning entities {}", entities);
        return entities;
    }

    private List<EmployeeEntity> convertSubordinatesToEntities(Set<Employee> subordinates, EmployeeEntity supervisorEntity) {
        List<EmployeeEntity> entities = new ArrayList<>();
        for (Employee subordinate : subordinates) {
            EmployeeEntity subordinateEntity = new EmployeeEntity(subordinate.getName(), supervisorEntity);
            List<EmployeeEntity> subordinateEntities = convertSubordinatesToEntities(subordinate.getSubordinates(), subordinateEntity);

            // Add top-level first then subordinates
            entities.add(subordinateEntity);
            entities.addAll(subordinateEntities);
        }
        return entities;
    }
}
