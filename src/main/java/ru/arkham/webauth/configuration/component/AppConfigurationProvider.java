package ru.arkham.webauth.configuration.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Провайдер конфигурации приложения.
 */
@Getter
@Component
public class AppConfigurationProvider {

    public static final String ROLE_NAME_DEFAULT = "USER";
    public static final String ROLE_NAME_ADMIN = "ADMIN";

    /**
     * Подписывающий ключ JWT.
     * Используется алгоритм HS512.
     */
    @Value("${app.jwt.secret-key}")
    private String jwtSecretKey;

    /**
     * Название токена в заголовке HTTP запроса.
     */
    @Value("${app.jwt.token.header}")
    private String jwtTokenHeader;

    /**
     * Префикс токена в заголовке HTTP запроса.
     */
    @Value("${app.jwt.token.prefix}")
    private String jwtTokenPrefix;

    /**
     * Жизненный цикл токена в секундах.
     */
    @Value("${app.jwt.token.lifetime-seconds}")
    private Long jwtTokenLifetimeSeconds;

    /**
     * Разрешенный источник CORS.
     */
    @Value("${app.cors.allowed-origin}")
    private String corsAllowedOrigin;
}
