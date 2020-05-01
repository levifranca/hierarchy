package com.everchanging.hierarchy.exception

import com.everchanging.hierarchy.dto.ValidationError
import java.lang.RuntimeException

data class HierarchyValidationException(val validationErrors: List<ValidationError>) : RuntimeException()