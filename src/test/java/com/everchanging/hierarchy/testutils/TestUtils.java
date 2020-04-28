package com.everchanging.hierarchy.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

@UtilityClass
public class TestUtils {

    public static String toJson(Object obj) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(obj);
    }

    public static HttpHeaders withBasicAuth() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("personia", "password");
        return httpHeaders;
    }
}
