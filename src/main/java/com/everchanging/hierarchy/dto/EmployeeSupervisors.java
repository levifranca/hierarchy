package com.everchanging.hierarchy.dto;

import lombok.Value;

@Value
public class EmployeeSupervisors {
    String name;
    String supervisor;
    String supervisorOfSupervisor;
}
