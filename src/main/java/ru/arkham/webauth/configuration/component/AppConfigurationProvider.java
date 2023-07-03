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

    /**
     * Подписывающий ключ.
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
     * Разрешенный источник.
     */
    @Value("${app.cors.allowed-origin}")
    private String corsAllowedOrigin;
}
