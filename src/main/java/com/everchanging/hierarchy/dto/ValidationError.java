package com.everchanging.hierarchy.dto;

import lombok.Data;
import lombok.Value;

@Value
public class ValidationError {
    String name;
    String message;
}
