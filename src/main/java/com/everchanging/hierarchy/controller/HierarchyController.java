package com.everchanging.hierarchy.controller;

import com.everchanging.hierarchy.converter.TopEmployeeToHierarchyMapConverter;
import com.everchanging.hierarchy.service.HierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class HierarchyController {

    private final TopEmployeeToHierarchyMapConverter topEmployeeToHierarchyMapConverter;
    private final HierarchyService hierarchyService;

    @PostMapping(
            path = "/hierarchy",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> computeHierarchy(@Valid @RequestBody Map<String, String> hierarchyRequest) {
        return topEmployeeToHierarchyMapConverter.convert(
                hierarchyService.computeAndSaveHierarchy(hierarchyRequest)
        );
    }
}
