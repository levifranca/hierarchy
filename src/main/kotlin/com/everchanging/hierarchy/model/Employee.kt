package com.everchanging.hierarchy.model

data class Employee(val name: String) {
    var supervisor: Employee? = null
        private set
    var subordinates: MutableSet<Employee> = mutableSetOf()
        private set

    fun addSubordinate(subordinate: Employee): Boolean {
        subordinate.supervisor = this
        return subordinates.add(subordinate)
    }
}