package com.everchanging.hierarchy.exception

import java.lang.RuntimeException

data class EmployeeNotFoundException(val employeeName: String) : RuntimeException()