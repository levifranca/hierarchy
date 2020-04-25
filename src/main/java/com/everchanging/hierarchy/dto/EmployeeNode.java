package com.everchanging.hierarchy.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeNode {
    private String name;

    private List<EmployeeNode> subordinates;

}
