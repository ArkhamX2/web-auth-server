package ru.arkham.webauth.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Тело запроса авторизации.
 */
@Data
public class LoginRequest {

    /**
     * Имя пользователя.
     */
    @NotBlank
    private String name;

    /**
     * Пароль пользователя.
     */
    @NotBlank
    private String password;
}
