package com.everchanging.hierarchy.converter;

import com.everchanging.hierarchy.dto.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public final class NamesLoopToValidationErrorConverterTest {

    private final NamesLoopToValidationErrorConverter converter = new NamesLoopToValidationErrorConverter();

    @Test
    @DisplayName("Should convert short name loop into ValidationError")
    public void testConvertShort() {
        // GIVEN
        ArrayList<String> nameLoop = new ArrayList<>(asList("Adam", "Barbara", "Adam"));

        // WHEN
        ValidationError convert = converter.convert(nameLoop);

        // THEN
        assertThat(convert).isNotNull();
        assertThat(convert.getName()).isEqualTo("HierarchyLoop");
        assertThat(convert.getMessage()).isEqualTo("Found loop with the Employees: [Adam -> Barbara -> Adam]");
    }

    @Test
    @DisplayName("Should convert long name loop into ValidationError")
    public void testConvertLong() {
        // GIVEN
        ArrayList<String> nameLoop = new ArrayList<>(asList(
                "Adam",
                "Barbara",
                "Carl",
                "Diego",
                "Eve",
                "Fernando",
                "Adam"));

        // WHEN
        ValidationError convert = converter.convert(nameLoop);

        // THEN
        assertThat(convert).isNotNull();
        assertThat(convert.getName()).isEqualTo("HierarchyLoop");
        assertThat(convert.getMessage()).isEqualTo("Found loop with the Employees: [Adam -> (4 employees) -> Fernando -> Adam]");
    }
}