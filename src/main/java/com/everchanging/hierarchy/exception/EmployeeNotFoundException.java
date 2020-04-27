package com.everchanging.hierarchy.exception;

import lombok.Getter;

public class EmployeeNotFoundException extends RuntimeException {

    @Getter
    private String employeeName;

    public EmployeeNotFoundException(String employeeName) {
        this.employeeName = employeeName;
    }
}
