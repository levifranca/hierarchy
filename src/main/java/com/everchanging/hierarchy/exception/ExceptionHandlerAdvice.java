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
}
