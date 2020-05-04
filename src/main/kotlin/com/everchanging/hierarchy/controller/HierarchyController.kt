package com.everchanging.hierarchy.controller

import com.everchanging.hierarchy.converter.TopEmployeeToHierarchyMapConverter
import com.everchanging.hierarchy.service.HierarchyService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class HierarchyController(
        private val hierarchyService: HierarchyService,
        private val topEmployeeToHierarchyMapConverter: TopEmployeeToHierarchyMapConverter) {

    @PostMapping(
            path = ["/hierarchy"],
            consumes = [MediaType.APPLICATION_JSON_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    fun computeHierarchy(@Valid @RequestBody hierarchyRequest: Map<String, String>): Map<String, Any> {
        return topEmployeeToHierarchyMapConverter.convert(
                hierarchyService.computeAndSaveHierarchy(hierarchyRequest)
        )
    }
}