package com.everchanging.hierarchy.exception;

import lombok.Getter;

public class EmployeeNotFoundException extends RuntimeException {

    @Getter
    private final String employeeName;

    public EmployeeNotFoundException(String employeeName) {
        super();
        this.employeeName = employeeName;
    }
}
