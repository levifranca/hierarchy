package com.everchanging.hierarchy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Employee {
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String name;

    private Employee supervisor;
    private Set<Employee> subordinates = new HashSet<>();

    public boolean addSubordinate(Employee employee) {
        return subordinates.add(employee);
    }
}
