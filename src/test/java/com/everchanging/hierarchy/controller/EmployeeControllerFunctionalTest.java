package com.everchanging.hierarchy.controller;

import com.everchanging.hierarchy.entity.EmployeeEntity;
import com.everchanging.hierarchy.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public final class EmployeeControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;

    @Test
    @DisplayName("Should return hierarchy given well-formed request")
    public void testSuccess() throws Exception {
        EmployeeEntity carlEntity = new EmployeeEntity("Carl", null);
        EmployeeEntity barbaraEntity = new EmployeeEntity("Barbara", carlEntity);
        EmployeeEntity adamEntity = new EmployeeEntity("Adam", barbaraEntity);
        repository.saveAll(List.of(carlEntity, barbaraEntity, adamEntity));

        String name = "Adam";

        mockMvc.perform(get("/employees/{name}/supervisors", name))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Adam")))
                .andExpect(jsonPath("supervisor", is("Barbara")))
                .andExpect(jsonPath("supervisorOfSupervisor", is("Carl")));

    }

    @Test
    @DisplayName("Should respond 404 when employee does not exist")
    public void testNotFound() throws Exception {

        String name = "zeno";

        mockMvc.perform(get("/employees/{name}/supervisors", name))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errors.size()", is(1)))
                .andExpect(jsonPath("errors[0].name", is("EmployeeNotFound")))
                .andExpect(jsonPath("errors[0].message", is("Could not find employee with name = 'zeno'")));

    }

}
