package ru.practicum.shareit.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HandleValidator {

    public static void handle(BindingResult bindingResult, Logger log) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            allErrors.stream()
                    .map(ObjectError::getDefaultMessage)
                    .forEach(log::error);
            String errors = allErrors.stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(";" + System.lineSeparator()));
            throw new ValidationException(errors);
        }
    }

}
