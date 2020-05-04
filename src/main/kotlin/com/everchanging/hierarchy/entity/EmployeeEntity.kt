package com.everchanging.hierarchy.entity

import javax.persistence.*

@Entity
@Table(name = "employees")
data class EmployeeEntity(
        @Id
        var name: String?,
        @OneToOne(
                fetch = FetchType.LAZY // do not load all upper level employees
        )
        private var supervisor: EmployeeEntity?) {
    constructor() : this(name = null, supervisor = null)
}