package com.everchanging.hierarchy.converter

import com.everchanging.hierarchy.dto.ValidationError
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.lang.StringBuilder

@Component
class NamesLoopToValidationErrorConverter : Converter<ArrayList<String>, ValidationError> {

    override fun convert(nameLoops: ArrayList<String>): ValidationError {
        val nameLoopMessage = buildNameLoopMessage(nameLoops)
        return ValidationError("HierarchyLoop", "Found loop with the Employees: [$nameLoopMessage]")
    }

    private fun buildNameLoopMessage(nameLoops: ArrayList<String>): String {
        if (nameLoops.size >= 5) {
            return "${nameLoops[0]} -> (${nameLoops.size - 3} employees) -> ${nameLoops[nameLoops.lastIndex - 1]} -> ${nameLoops[nameLoops.lastIndex]}"
        }
        val message = StringBuilder()
        for ((idx, name) in nameLoops.withIndex()) {
            message.append(name)
            if (idx != nameLoops.lastIndex) {
                message.append(" -> ")
            }
        }
        return message.toString()
    }

}