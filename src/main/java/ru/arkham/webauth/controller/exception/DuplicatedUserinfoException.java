package ru.arkham.webauth.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, обозначающее дублирование информации о пользователе.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedUserinfoException extends RuntimeException {

    /**
     * Конструктор.
     * @param message сообщение.
     */
    public DuplicatedUserinfoException(String message) {
        super(message);
    }
}
