package ru.arkham.webauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, обозначающее ошибку поиска информации о пользователе.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Конструктор.
     * @param message сообщение.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
