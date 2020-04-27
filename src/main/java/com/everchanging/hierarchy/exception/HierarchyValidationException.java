package com.everchanging.hierarchy.exception;

import com.everchanging.hierarchy.dto.ValidationError;
import lombok.Getter;

import java.util.List;

public class HierarchyValidationException extends RuntimeException {
    @Getter
    final transient List<ValidationError> validationErrors;

    public HierarchyValidationException(List<ValidationError> validationErrors) {
        super();
        this.validationErrors = validationErrors;
    }
}
