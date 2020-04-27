package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.model.Employee;
import com.everchanging.hierarchy.validator.HierarchyValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class HierarchyServiceTest {

    @Mock
    private HierarchyValidator hierarchyValidator;

    @InjectMocks
    private HierarchyService service;

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
        Employee topLevelEmployee = service.computeHierarchy(employeesHierarchyListing);

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
    }
/*
    @Test
    @DisplayName("Should detect single hierarchy loop")
    public void testHierarchyLoop() {
        // GIVEN
        Map<String, String> employeesHierarchyListing = Map.of(
                "Pete", "Nick",
                "Nick", "Sophie",
                "Barbara", "Sophie",
                "Sophie", "Pete"
        );

        // WHEN
        HierarchyValidationException exception = assertThrows(HierarchyValidationException.class,
                () -> service.computeHierarchy(employeesHierarchyListing));

        // THEN
        assertThat(exception.getValidationErrors()).hasSize(1);
        assertThat(exception.getValidationErrors().get(0).getName()).isEqualTo();
        assertThat(exception.getValidationErrors().get(0).getName()).isEqualTo();
    }

    @Test
    @DisplayName("Should detect multiple hierarchy loop")
    public void testMultipleHierarchyLoop() {
        // GIVEN
        Map<String, String> employeesHierarchyListing = Map.of(
                // loop 1
                "Adam", "Barbara",
                "Barbara", "Adam",
                // loop 2
                "Carl", "Diego",
                "Diego", "Carl",

                "Eve", "Fernando",
                "Fernando", "Xerxes",
                // loop 3
                "Xerxes", "Yasmin",
                "Yasmin", "Zeno",
                "Zeno", "Xerxes"

        );

        // WHEN
        HierarchyLoopException exception = assertThrows(HierarchyLoopException.class,
                () -> service.computeHierarchy(employeesHierarchyListing));

        // THEN
        assertThat(exception.getEmployeeNameLoops()).hasSize(3);
        assertThat(exception.getEmployeeNameLoops())
                .filteredOnAssertions(list -> assertThat(list).contains("Adam", "Barbara", "Adam")).isNotEmpty();
        assertThat(exception.getEmployeeNameLoops())
                .filteredOnAssertions(list -> assertThat(list).contains("Diego", "Carl")).isNotEmpty();
        assertThat(exception.getEmployeeNameLoops())
                .filteredOnAssertions(list -> assertThat(list).contains("Xerxes", "Yasmin", "Zeno")).isNotEmpty();
    }
*/
    private void assertSubordinates(Employee employee, Set<String> expectedSubordinates) {
        Set<String> actualSubordinates = employee.getSubordinates().stream().map(Employee::getName).collect(toSet());

        assertThat(actualSubordinates).isEqualTo(expectedSubordinates);
    }

}