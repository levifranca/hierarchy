package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

class HierarchyServiceTest {

    private final HierarchyService service = new HierarchyService();

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

    private void assertSubordinates(Employee employee, Set<String> expectedSubordinates) {
        Set<String> actualSubordinates = employee.getSubordinates().stream().map(Employee::getName).collect(toSet());

        assertThat(actualSubordinates).isEqualTo(expectedSubordinates);
    }

}