package com.everchanging.hierarchy.exception;

import com.everchanging.hierarchy.dto.ValidationError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

public class HierarchyValidationException extends RuntimeException {
    @Getter
    final List<ValidationError> validationErrors;

    public HierarchyValidationException(List<ValidationError> validationErrors) {
        super();
        this.validationErrors = validationErrors;
    }
}
