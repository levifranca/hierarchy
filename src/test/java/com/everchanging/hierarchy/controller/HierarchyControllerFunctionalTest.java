package com.everchanging.hierarchy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class HierarchyControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return hierarchy given well-formed request")
    void testSuccess() throws Exception{

        Map<String, String> requestBody = Map.of(
                "Pete", "Nick",
                "Barbara", "Nick",
                "Nick", "Sophie",
                "Sophie", "Jonas"
        );

        mockMvc.perform(post("/hierarchy")
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

    }

    private static String toJson(Object obj) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(obj);
    }

}
