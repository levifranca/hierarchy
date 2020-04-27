package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public final class TopEmployeeToHierarchyMapConverterTest {

    private final TopEmployeeToHierarchyMapConverter converter = new TopEmployeeToHierarchyMapConverter();

    @Test
    @DisplayName("Should convert employee into Hierarchy map")
    public void testSuccess() {
        // GIVEN
        Employee rootEmployee = new Employee("Adam");
        Employee barbara = new Employee("Barbara");
        rootEmployee.addSubordinate(barbara);
        rootEmployee.addSubordinate(new Employee("Charles"));

        barbara.addSubordinate(new Employee("Damon"));

        // WHEN
        Map<String, Object> hierarchy = converter.convert(rootEmployee);

        // THEN
        assertThat(hierarchy).containsKeys("Adam");
        Map<String, Object> adamHierarchy = (Map<String, Object>) hierarchy.get("Adam");
        assertThat(adamHierarchy).containsKeys("Barbara", "Charles");
        Map<String, Object> barbaraHierarchy = (Map<String, Object>) adamHierarchy.get("Barbara");
        assertThat(barbaraHierarchy).containsKeys("Damon");
        Object charlesHierarchy = adamHierarchy.get("Charles");
        assertThat(charlesHierarchy).isExactlyInstanceOf(Object.class);
        Object damonHierarchy = barbaraHierarchy.get("Damon");
        assertThat(damonHierarchy).isExactlyInstanceOf(Object.class);
    }
}