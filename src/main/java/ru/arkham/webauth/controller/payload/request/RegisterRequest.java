package ru.arkham.webauth.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.arkham.webauth.configuration.component.AppConfigurationProvider;

/**
 * Тело запроса регистрации.
 */
@Data
public class RegisterRequest {

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

    /**
     * Название роли пользователя.
     */
    @NotBlank
    private String roleName = AppConfigurationProvider.ROLE_NAME_DEFAULT;
}
