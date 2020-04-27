package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.converter.TopEmployeeToEntityListConverter;
import com.everchanging.hierarchy.dto.EmployeeSupervisors;
import com.everchanging.hierarchy.entity.EmployeeEntity;
import com.everchanging.hierarchy.exception.EmployeeNotFoundException;
import com.everchanging.hierarchy.model.Employee;
import com.everchanging.hierarchy.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public final class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private TopEmployeeToEntityListConverter converter;

    @InjectMocks
    private EmployeeService service;

    @Test
    @DisplayName("Should convert to list, delete and save all when refreshing hierarchy")
    public void testRefreshAll() {
        // GIVEN
        Employee topLevelEmployee = new Employee("Adam");
        Employee barbara = new Employee("Barbara");
        topLevelEmployee.addSubordinate(barbara);

        List<EmployeeEntity> entities = asList(mock(EmployeeEntity.class), mock(EmployeeEntity.class));
        given(converter.convert(topLevelEmployee)).willReturn(entities);

        // WHEN
        service.refreshHierarchy(topLevelEmployee);

        // THEN
        verify(converter).convert(topLevelEmployee);
        verify(repository).deleteAll();
        verify(repository).saveAll(entities);
    }

    @Test
    @DisplayName("Should return supervisors when found")
    public void testGetSupervisors() {
        // GIVEN
        String name = "Adam";
        EmployeeSupervisors expectedSupervisors = new EmployeeSupervisors("Adam", "Barbara", null);
        given(repository.getSupervisors(name)).willReturn(expectedSupervisors);

        // WHEN
        EmployeeSupervisors supervisors = service.getSupervisors(name);

        // THEN
        assertThat(supervisors).isEqualTo(expectedSupervisors);
    }

    @Test
    @DisplayName("Should throw not found when supervisors not found by repository")
    public void testGetSupervisorsNotFound() {
        // GIVEN
        String name = "Adam";

        given(repository.getSupervisors(name)).willReturn(null);

        // WHEN
        EmployeeNotFoundException notFoundException = assertThrows(EmployeeNotFoundException.class,
                () -> service.getSupervisors(name)
        );

        // THEN
        assertThat(notFoundException.getEmployeeName()).isEqualTo(name);
    }
}