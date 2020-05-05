package com.everchanging.hierarchy.converter

import com.everchanging.hierarchy.model.Employee
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class TopEmployeeToHierarchyMapConverter : Converter<Employee, Map<String, Any>> {
    companion object {
        private val log = LoggerFactory.getLogger(TopEmployeeToHierarchyMapConverter::class.java)
    }

    private val emptyObject: Any = Object()

    override fun convert(topLevelEmployee: Employee): Map<String, Any> {
        log.debug("Convert from top topLevelEmployee $topLevelEmployee to hierarchy map")
        if (topLevelEmployee.supervisor != null) {
            log.error("Received a non-top topLevelEmployee to convert into hierarchy map. $topLevelEmployee")
            throw IllegalStateException()
        }

        return mapOf(topLevelEmployee.name to convertList(topLevelEmployee.subordinates))
    }

    private fun convertList(employees: Set<Employee>): Any {
        if (employees.isEmpty()) {
            return emptyObject
        }

        return employees.map { it.name to convertList(it.subordinates) }.toMap()
    }
}