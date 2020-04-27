package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.entity.EmployeeEntity;
import com.everchanging.hierarchy.model.Employee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class TopEmployeeToEntityListConverter implements Converter<Employee, List<EmployeeEntity>> {
    @Override
    public List<EmployeeEntity> convert(Employee topLevelEmployee) {
        List<EmployeeEntity> entities = new ArrayList<>();
        EmployeeEntity employeeEntity = new EmployeeEntity(topLevelEmployee.getName(), null);
        List<EmployeeEntity> subordinateEntities = convertSubordinatesToEntities(topLevelEmployee.getSubordinates(), employeeEntity);

        // Add top-level first then subordinates
        entities.add(employeeEntity);
        entities.addAll(subordinateEntities);
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
