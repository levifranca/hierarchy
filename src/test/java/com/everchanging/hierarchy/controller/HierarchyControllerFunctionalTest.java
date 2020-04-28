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
import java.util.Map;

import static com.everchanging.hierarchy.testutils.TestUtils.toJson;
import static com.everchanging.hierarchy.testutils.TestUtils.withBasicAuth;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public final class HierarchyControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;

    @Test
    @DisplayName("Should return hierarchy given well-formed request")
    public void testCreateHierarchy() throws Exception {

        Map<String, String> requestBody = Map.of(
                "Pete", "Nick",
                "Barbara", "Nick",
                "Nick", "Sophie",
                "Sophie", "Jonas"
        );

        mockMvc.perform(post("/hierarchy")
                        .headers(withBasicAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(requestBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("Jonas", hasKey("Sophie")))
                .andExpect(jsonPath("Jonas.Sophie", hasKey("Nick")))
                .andExpect(jsonPath("Jonas.Sophie.Nick", hasKey("Pete")))
                .andExpect(jsonPath("Jonas.Sophie.Nick", hasKey("Barbara")))
                .andExpect(jsonPath("Jonas.Sophie.Nick.Pete.size()", is(0)))
                .andExpect(jsonPath("Jonas.Sophie.Nick.Barbara.size()", is(0)));

        List<EmployeeEntity> entities = repository.findAll();
        assertThat(entities).hasSize(5);
        Map<String, EmployeeEntity> entityToNameMap = entities.stream().collect(toMap(EmployeeEntity::getName, identity()));
        EmployeeEntity jonas = entityToNameMap.get("Jonas");
        assertThat(jonas).isEqualTo(new EmployeeEntity("Jonas", null));
        EmployeeEntity sophie = entityToNameMap.get("Sophie");
        assertThat(sophie).isEqualTo(new EmployeeEntity("Sophie", jonas));
        EmployeeEntity nick = entityToNameMap.get("Nick");
        assertThat(nick).isEqualTo(new EmployeeEntity("Nick", sophie));
        EmployeeEntity pete = entityToNameMap.get("Pete");
        assertThat(pete).isEqualTo(new EmployeeEntity("Pete", nick));
        EmployeeEntity barbara = entityToNameMap.get("Barbara");
        assertThat(barbara).isEqualTo(new EmployeeEntity("Barbara", nick));
    }

    @Test
    @DisplayName("Should return error on duplicate keys")
    public void testDuplicateKeys() throws Exception {

        String requestBody = "{" +
                "\"Adam\":\"Barbara\"," +
                "\"Adam\":\"Charles\"," +
            "}";

        mockMvc.perform(post("/hierarchy")
                        .headers(withBasicAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errors.size()", is(1)))
                .andExpect(jsonPath("errors[0].name", is("InvalidJsonRequest")))
                .andExpect(jsonPath("errors[0].message", startsWith("Duplicate field 'Adam'")));

    }

    @Test
    @DisplayName("Should return error on malformed request")
    public void testMalformed() throws Exception {

        String requestBody = "{" +
                "\"Adam\":[\"Barbara\"," +
                "}";

        mockMvc.perform(post("/hierarchy")
                        .headers(withBasicAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errors.size()", is(1)))
                .andExpect(jsonPath("errors[0].name", is("InvalidJsonRequest")))
                .andExpect(jsonPath("errors[0].message", startsWith("Cannot deserialize")));

    }

    @Test
    @DisplayName("Should return validation errors on invalid hierarchy")
    public void testInvalidHierarchy() throws Exception {

        Map<String, String> requestBody = Map.of(
                "Barbara", "Adam",
                "Carl", "Barbara",
                "Adam", "Carl",
                "Xerxes", "Yasmin",
                "Will", "Zeno"
        );

        mockMvc.perform(post("/hierarchy")
                        .headers(withBasicAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(requestBody)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errors.size()", is(2)))
                .andExpect(jsonPath("errors[*].name", containsInAnyOrder("HierarchyLoop", "HierarchyInvalidRoot")))
                .andExpect(jsonPath("errors[*].message", containsInAnyOrder(
                        startsWith("Found loop with the Employees:"),
                        startsWith("Found 2 roots in the hierarchy. Roots employees are"))));

    }

    @Test
    @DisplayName("Should respond 401 when auth data is not provided")
    public void testUnauthorized() throws Exception {

        mockMvc.perform(post("/hierarchy"))
                .andExpect(status().isUnauthorized());

    }
}
