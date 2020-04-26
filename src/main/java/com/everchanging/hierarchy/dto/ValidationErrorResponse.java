package com.everchanging.hierarchy.dto;

import lombok.Value;

import java.util.List;

@Value
public class ValidationErrorResponse {
    List<ValidationError> errors;
}
