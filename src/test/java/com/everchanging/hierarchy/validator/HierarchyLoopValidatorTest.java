package com.everchanging.hierarchy.validator;

import com.everchanging.hierarchy.converter.NamesLoopToValidationErrorConverter;
import com.everchanging.hierarchy.dto.ValidationError;
import com.everchanging.hierarchy.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HierarchyLoopValidatorTest {
    private static final ValidationError MOCK_VALIDATION_ERROR = new ValidationError("", "");

    @Mock
    private NamesLoopToValidationErrorConverter converter;

    @InjectMocks
    private HierarchyLoopValidator validator;

    @Captor
    private ArgumentCaptor<ArrayList<String>> nameLoopsCaptor;

    @Test
    @DisplayName("Should return empty list on valid hierarchy")
    public void testNoLoop() {
        // GIVEN
        Employee pete = new Employee("Pete");
        Employee nick = new Employee("Nick");
        Employee barbara = new Employee("Barbara");
        Employee sophie = new Employee("Sophie");

        nick.addSubordinate(pete);
        sophie.addSubordinate(nick);
        sophie.addSubordinate(barbara);

        Set<Employee> employees = Set.of(pete, nick, barbara, sophie);

        // WHEN
        List<ValidationError> validationErrors = validator.validate(employees);

        // THEN
        assertThat(validationErrors).isEmpty();
        verifyNoInteractions(converter);
    }

    @Test
    @DisplayName("Should detect single hierarchy loop")
    public void testSingleLoop() {
        // GIVEN
        Employee pete = new Employee("Pete");
        Employee nick = new Employee("Nick");
        Employee barbara = new Employee("Barbara");
        Employee sophie = new Employee("Sophie");

        nick.addSubordinate(pete);
        pete.addSubordinate(sophie);
        sophie.addSubordinate(nick);
        sophie.addSubordinate(barbara);

        Set<Employee> employees = Set.of(pete, nick, barbara, sophie);

        given(converter.convert(ArgumentMatchers.any()))
                .willReturn(MOCK_VALIDATION_ERROR);

        // WHEN
        List<ValidationError> validationErrors = validator.validate(employees);

        // THEN
        assertThat(validationErrors).hasSize(1);
        verify(converter).convert(nameLoopsCaptor.capture());
        assertThat(nameLoopsCaptor.getValue()).contains("Pete", "Nick", "Sophie");
    }

    @Test
    @DisplayName("Should detect multiple hierarchy loop")
    public void testMultipleLoop() {
        // GIVEN
        Employee adam = new Employee("Adam");
        Employee barbara = new Employee("Barbara");
        Employee carl = new Employee("Carl");
        Employee diego = new Employee("Diego");
        Employee eve = new Employee("Eve");
        Employee fernando = new Employee("Fernando");
        Employee xerxes = new Employee("Xerxes");
        Employee yasmin = new Employee("Yasmin");
        Employee zeno = new Employee("Zeno");

        // loop 1
        barbara.addSubordinate(adam);
        adam.addSubordinate(barbara);
        // loop 2
        diego.addSubordinate(carl);
        carl.addSubordinate(diego);
        //loop 3
        yasmin.addSubordinate(xerxes);
        zeno.addSubordinate(yasmin);
        xerxes.addSubordinate(zeno);
        // no loop
        fernando.addSubordinate(eve);
        xerxes.addSubordinate(fernando);

        Set<Employee> employees = Set.of(adam, barbara, carl, diego, eve, fernando, xerxes, yasmin, zeno);

        given(converter.convert(ArgumentMatchers.any()))
                .willReturn(MOCK_VALIDATION_ERROR);

        // WHEN
        List<ValidationError> validationErrors = validator.validate(employees);

        // THEN
        assertThat(validationErrors).hasSize(3);

        verify(converter, times(3)).convert(nameLoopsCaptor.capture());
        List<ArrayList<String>> nameLoops = nameLoopsCaptor.getAllValues();
        assertThat(nameLoops).hasSize(3);
        assertThat(nameLoops)
                .filteredOnAssertions(list -> assertThat(list).contains("Adam", "Barbara", "Adam")).isNotEmpty();
        assertThat(nameLoops)
                .filteredOnAssertions(list -> assertThat(list).contains("Diego", "Carl")).isNotEmpty();
        assertThat(nameLoops)
                .filteredOnAssertions(list -> assertThat(list).contains("Xerxes", "Yasmin", "Zeno")).isNotEmpty();
    }
}