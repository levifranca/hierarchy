package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.dto.ValidationError;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static java.lang.String.format;

@Component
public class NamesLoopToValidationErrorConverter implements Converter<ArrayList<String>, ValidationError> {
    @Override
    public ValidationError convert(ArrayList<String> nameLoops) {
        String nameLoopMessage = buildNameLoopMessage(nameLoops);
        return new ValidationError("HierarchyLoop", format("Found loop with the Employees: [%s]", nameLoopMessage));
    }

    private String buildNameLoopMessage(ArrayList<String> nameLoops) {
        if (nameLoops.size() >= 5) {
            return format("%s -> (%d employees) -> %s -> %s",
                    nameLoops.get(0), nameLoops.size()-3, nameLoops.get(nameLoops.size()-2), nameLoops.get(nameLoops.size()-1));
        }
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < nameLoops.size(); i++) {
            String name = nameLoops.get(i);
            message.append(name);
            if (i != nameLoops.size()-1) {
                message.append(" -> ");
            }
        }
        return message.toString();
    }
}
