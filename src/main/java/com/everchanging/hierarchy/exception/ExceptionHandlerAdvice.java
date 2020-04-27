package com.everchanging.hierarchy.exception;

import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.dto.ValidationErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static java.lang.String.format;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonParseException.class)
    @ResponseBody
    public ValidationErrorResponse handleParseErrors(JsonParseException e) {
        return new ValidationErrorResponse(List.of(new ValidationError("InvalidJsonRequest", e.getMessage())));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MismatchedInputException.class)
    @ResponseBody
    public ValidationErrorResponse handleParseErrors(MismatchedInputException e) {
        return new ValidationErrorResponse(List.of(new ValidationError("InvalidJsonRequest", e.getMessage())));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HierarchyValidationException.class)
    @ResponseBody
    public ValidationErrorResponse handleHierarchyValidation(HierarchyValidationException e) {
        return new ValidationErrorResponse(e.getValidationErrors());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseBody
    public ValidationErrorResponse handleEmployeeNotFound(EmployeeNotFoundException e) {
        return new ValidationErrorResponse(List.of(
                new ValidationError("EmployeeNotFound",
                        format("Could not find employee with name = '%s'", e.getEmployeeName()))));
    }
}
