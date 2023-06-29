package ru.arkham.webchat.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.arkham.webchat.configuration.component.TokenProvider;

import java.util.List;

/**
 * Конфигуратор бинов для модуля CORS.
 */
@Configuration
public class CorsConfigurer {

    /**
     * Разрешенный источник.
     */
    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    /**
     * Конфигурация CORS.
     * @return конфигурация CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(List.of(TokenProvider.TOKEN_HEADER));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // TODO: Разместить все пути в одном месте.
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
