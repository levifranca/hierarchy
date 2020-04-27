package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.converter.TopEmployeeToEntityListConverter;
import com.everchanging.hierarchy.entity.EmployeeEntity;
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

}