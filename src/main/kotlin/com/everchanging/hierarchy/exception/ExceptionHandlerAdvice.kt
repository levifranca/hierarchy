package com.everchanging.hierarchy.exception

import com.everchanging.hierarchy.dto.ValidationError
import com.everchanging.hierarchy.dto.ValidationErrorResponse
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionHandlerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonParseException::class)
    @ResponseBody
    fun handleParseErrors(e: JsonParseException): ValidationErrorResponse {
        return ValidationErrorResponse(listOf(ValidationError("InvalidJsonRequest", e.message ?: "Parse error found")))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MismatchedInputException::class)
    @ResponseBody
    fun handleParseErrors(e: MismatchedInputException): ValidationErrorResponse {
        return ValidationErrorResponse(listOf(ValidationError("InvalidJsonRequest",
                e.message ?: "Mismatched Input found")))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HierarchyValidationException::class)
    @ResponseBody
    fun handleHierarchyValidation(e: HierarchyValidationException): ValidationErrorResponse {
        return ValidationErrorResponse(e.validationErrors)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmployeeNotFoundException::class)
    @ResponseBody
    fun handleEmployeeNotFound(e: EmployeeNotFoundException): ValidationErrorResponse {
        return ValidationErrorResponse(listOf(
                ValidationError("EmployeeNotFound", "Could not find employee with name = '${e.employeeName}'")))
    }
}