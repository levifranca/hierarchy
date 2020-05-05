package com.everchanging.hierarchy.converter

import com.everchanging.hierarchy.entity.EmployeeEntity
import com.everchanging.hierarchy.model.Employee
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class TopEmployeeToEntityListConverter : Converter<Employee, List<EmployeeEntity>> {
    companion object {
        private val log = LoggerFactory.getLogger(TopEmployeeToEntityListConverter::class.java)
    }

    override fun convert(topLevelEmployee: Employee): List<EmployeeEntity> {
        log.debug("Convert from top topLevelEmployee $topLevelEmployee to list of entities")
        if (topLevelEmployee.supervisor != null) {
            log.error("Received a non-top topLevelEmployee to convert into hierarchy map. $topLevelEmployee")
            throw IllegalStateException()
        }

        val entities = arrayListOf<EmployeeEntity>()
        val employeeEntity = EmployeeEntity(topLevelEmployee.name)
        val subordinateEntities = convertSubordinatesToEntities(topLevelEmployee.subordinates, employeeEntity)

        // Add top-level first then subordinates
        entities.add(employeeEntity)
        entities.addAll(subordinateEntities)

        log.debug("Returning entities $entities")
        return entities
    }

    private fun convertSubordinatesToEntities(subordinates: Set<Employee>, supervisorEntity: EmployeeEntity): List<EmployeeEntity> {
        val entities = arrayListOf<EmployeeEntity>()

        for (subordinate in subordinates) {
            val subordinateEntity = EmployeeEntity(subordinate.name, supervisorEntity)
            val subordinateEntities = convertSubordinatesToEntities(subordinate.subordinates, subordinateEntity)

            // Add top-level first then subordinates
            entities.add(subordinateEntity)
            entities.addAll(subordinateEntities)
        }
        return entities
    }
}