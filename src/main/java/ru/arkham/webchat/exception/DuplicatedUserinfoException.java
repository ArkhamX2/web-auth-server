package ru.arkham.webchat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, обозначающее дублирование информации о пользователе.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedUserinfoException extends RuntimeException {

    public DuplicatedUserinfoException(String message) {
        super(message);
    }
}
