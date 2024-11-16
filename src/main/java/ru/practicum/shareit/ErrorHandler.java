package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final ForbiddenException e) {
        log.error("Ошибка при редактировании объекта: недостаточно прав.", e);
        return new ErrorResponse("У вас недостаточно прав для редактирования этой вещи.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка при обработке запроса, один из объектов переданных запросе не найден (не существует).", e);
        return new ErrorResponse("Объект не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicatedDataException(final DuplicatedDataException e) {
        log.error("Ошибка при создании объекта. Объект с такими данными уже существует.", e);
        return new ErrorResponse("Пользователь с таким email уже существует.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExistingDataException(final ExistingDataException e) {
        log.error("Ошибка при обновлении объекта. Объект с такими данными уже существует.", e);
        return new ErrorResponse("Этот email уже занят.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationDataException(final ValidationException e) {
        log.error("Ошибка при создании или изменении объекта. %s".formatted(e.getMessage()), e);
        return new ErrorResponse("Ошибка при создании или изменении объекта.", e.getMessage());
    }
}
