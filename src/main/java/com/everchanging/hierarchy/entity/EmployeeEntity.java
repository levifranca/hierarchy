package com.everchanging.hierarchy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class EmployeeEntity {
    @Id
    private String name;

    @OneToOne(
            fetch = FetchType.LAZY // do not load all upper level employees
    )
    private EmployeeEntity supervisor;
}
