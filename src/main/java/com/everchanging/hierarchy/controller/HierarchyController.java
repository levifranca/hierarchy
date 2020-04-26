package com.everchanging.hierarchy.controller;

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

    private final HierarchyService hierarchyService;

    @PostMapping(
            path = "/hierarchy",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> hierarchy(@Valid @RequestBody Map<String, String> hierarchyRequest) {
        return hierarchyService.createHierarchy(hierarchyRequest);
    }
}
