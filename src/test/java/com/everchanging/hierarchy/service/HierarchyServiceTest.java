package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.exception.HierarchyValidationException;
import com.everchanging.hierarchy.model.Employee;
import com.everchanging.hierarchy.validator.HierarchyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public final class HierarchyServiceTest {

    private final HierarchyValidator validator1 = mock(HierarchyValidator.class);
    private final HierarchyValidator validator2 = mock(HierarchyValidator.class);
    private final EmployeeService employeeService = mock(EmployeeService.class);

    private HierarchyService service;

    @BeforeEach
    public void setup() {
        service = new HierarchyService(asList(validator1, validator2), employeeService);
    }

    @Test
    @DisplayName("Should compute employee's hierarchy successfully")
    public void testSuccessComputeHierarchy() {
        // GIVEN
        Map<String, String> employeesHierarchyListing = Map.of(
                "Pete", "Nick",
                "Barbara", "Nick",
                "Nick", "Sophie",
                "Sophie", "Jonas"
        );

        // WHEN
        Employee topLevelEmployee = service.computeAndSaveHierarchy(employeesHierarchyListing);

        // THEN
        assertThat(topLevelEmployee).isNotNull();
        assertThat(topLevelEmployee.getName()).isEqualTo("Jonas");
        assertSubordinates(topLevelEmployee, Set.of("Sophie"));

        Map<String, Employee> subordinateMap = topLevelEmployee.getSubordinates().stream()
                .collect(toMap(Employee::getName, Function.identity()));
        Employee sophie = subordinateMap.get("Sophie");
        assertThat(sophie).isNotNull();
        assertSubordinates(sophie, Set.of("Nick"));

        subordinateMap = sophie.getSubordinates().stream()
                .collect(toMap(Employee::getName, Function.identity()));
        Employee nick = subordinateMap.get("Nick");
        assertThat(nick).isNotNull();
        assertSubordinates(nick, Set.of("Pete", "Barbara"));

        subordinateMap = nick.getSubordinates().stream()
                .collect(toMap(Employee::getName, Function.identity()));
        Employee pete = subordinateMap.get("Pete");
        assertThat(pete).isNotNull();
        assertThat(pete.getSubordinates()).isEmpty();
        Employee barbara = subordinateMap.get("Barbara");
        assertThat(barbara).isNotNull();
        assertThat(barbara.getSubordinates()).isEmpty();

        verify(employeeService).refreshHierarchy(topLevelEmployee);
    }

    @Test
    @DisplayName("Should throw exception on validation errors")
    public void testValidationError() {
        // GIVEN
        Map<String, String> employeesHierarchyListing = Map.of(
                "Pete", "Nick",
                "Nick", "Pete"
        );

        ValidationError validationError1 = new ValidationError("name1", "message1");
        ValidationError validationError2 = new ValidationError("name2", "message2");
        ValidationError validationError3 = new ValidationError("name3", "message3");
        given(validator1.validate(anySet())).willReturn(Collections.singletonList(validationError1));
        given(validator2.validate(anySet())).willReturn(List.of(validationError2, validationError3));

        // WHEN
        HierarchyValidationException hierarchyValidationException = assertThrows(HierarchyValidationException.class,
                () -> service.computeAndSaveHierarchy(employeesHierarchyListing));

        // THEN
        assertThat(hierarchyValidationException.getValidationErrors()).hasSize(3);
        assertThat(hierarchyValidationException.getValidationErrors())
                .containsExactlyInAnyOrder(validationError1, validationError2, validationError3);

        verifyNoInteractions(employeeService);
    }

    private void assertSubordinates(Employee employee, Set<String> expectedSubordinates) {
        Set<String> actualSubordinates = employee.getSubordinates().stream().map(Employee::getName).collect(toSet());

        assertThat(actualSubordinates).isEqualTo(expectedSubordinates);
    }

}