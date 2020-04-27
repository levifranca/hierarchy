package com.everchanging.hierarchy.validator;

import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public final class HierarchyRootValidatorTest {

    private final HierarchyRootValidator validator = new HierarchyRootValidator();

    @Test
    @DisplayName("Should return empty list on valid employees")
    public void testValid() {
        // GIVEN
        Employee pete = new Employee("Pete");
        Employee nick = new Employee("Nick");
        Employee barbara = new Employee("Barbara");
        Employee sophie = new Employee("Sophie");

        nick.addSubordinate(pete);
        sophie.addSubordinate(nick);
        sophie.addSubordinate(barbara);

        Set<Employee> employees = Set.of(pete, nick, barbara, sophie);

        // WHEN
        List<ValidationError> validationErrors = validator.validate(employees);

        // THEN
        assertThat(validationErrors).isEmpty();
    }

    @Test
    @DisplayName("Should return single error when there is no root")
    public void testNoRoot() {
        // GIVEN
        Employee pete = new Employee("Pete");
        Employee nick = new Employee("Nick");
        Employee barbara = new Employee("Barbara");

        nick.addSubordinate(pete);
        pete.addSubordinate(barbara);
        barbara.addSubordinate(nick);

        Set<Employee> employees = Set.of(pete, nick, barbara);

        // WHEN
        List<ValidationError> validationErrors = validator.validate(employees);

        // THEN
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0)).isEqualTo(new ValidationError("HierarchyInvalidRoot", "No hierarchy root found."));
    }

    @Test
    @DisplayName("Should return single error when there is more than one root")
    public void testMultipleRoot() {
        // GIVEN
        Employee pete = new Employee("Pete");
        Employee nick = new Employee("Nick");
        Employee barbara = new Employee("Barbara");
        Employee sophie = new Employee("Sophie");

        nick.addSubordinate(pete);
        barbara.addSubordinate(sophie);

        Set<Employee> employees = Set.of(pete, nick, barbara);

        // WHEN
        List<ValidationError> validationErrors = validator.validate(employees);

        // THEN
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).getName()).isEqualTo("HierarchyInvalidRoot");
        assertThat(validationErrors.get(0).getMessage()).isEqualTo("Found 2 roots in the hierarchy. Roots employees are [Barbara, Nick]");
    }
}