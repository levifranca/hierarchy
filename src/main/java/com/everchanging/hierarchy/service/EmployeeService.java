package com.everchanging.hierarchy.service;

import com.everchanging.hierarchy.converter.TopEmployeeToEntityListConverter;
import com.everchanging.hierarchy.dto.EmployeeSupervisors;
import com.everchanging.hierarchy.entity.EmployeeEntity;
import com.everchanging.hierarchy.exception.EmployeeNotFoundException;
import com.everchanging.hierarchy.model.Employee;
import com.everchanging.hierarchy.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final TopEmployeeToEntityListConverter converter;

    @Transactional
    public void refreshHierarchy(Employee topLevelEmployee) {
        List<EmployeeEntity> entities = converter.convert(topLevelEmployee);

        repository.deleteAll();
        repository.saveAll(entities);
    }

    public EmployeeSupervisors getSupervisors(@NotNull String employeeName) {
        EmployeeSupervisors supervisors = repository.getSupervisors(employeeName);
        if (supervisors == null) {
            throw new EmployeeNotFoundException(employeeName);
        }
        return supervisors;
    }
}
