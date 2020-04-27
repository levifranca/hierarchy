package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.entity.EmployeeEntity;
import com.everchanging.hierarchy.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class TopEmployeeToEntityListConverterTest {

    private final TopEmployeeToEntityListConverter converter = new TopEmployeeToEntityListConverter();

    @Test
    @DisplayName("Should convert top level employee into sorted list of entities")
    public void testConvert() {
        // GIVEN
        Employee topLevelEmployee = new Employee("Adam");
        Employee barbara = new Employee("Barbara");
        Employee carl = new Employee("Carl");
        Employee diego = new Employee("Diego");

        topLevelEmployee.addSubordinate(barbara);
        topLevelEmployee.addSubordinate(carl);
        carl.addSubordinate(diego);

        // WHEN
        List<EmployeeEntity> entities = converter.convert(topLevelEmployee);

        // THEN
        assertThat(entities).hasSize(4);
        EmployeeEntity adamEntity = new EmployeeEntity("Adam", null);
        EmployeeEntity barbaraEntity = new EmployeeEntity("Barbara", adamEntity);
        EmployeeEntity carlEntity = new EmployeeEntity("Carl", adamEntity);
        EmployeeEntity diegoEntity = new EmployeeEntity("Diego", carlEntity);
        assertThat(entities).containsExactly(adamEntity, barbaraEntity, carlEntity, diegoEntity);

    }
}