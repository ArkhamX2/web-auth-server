package ru.arkham.webauth.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.arkham.webauth.configuration.component.AppConfigurationProvider;
import ru.arkham.webauth.configuration.component.EndpointProvider;

import java.util.List;

/**
 * Конфигуратор бинов для модуля CORS.
 */
@RequiredArgsConstructor
@Configuration
public class CorsConfigurer {

    /**
     * Провайдер конфигурации приложения.
     */
    private final AppConfigurationProvider appConfigurationProvider;

    /**
     * Получить конфигурацию CORS.
     * @return конфигурация CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(appConfigurationProvider.getCorsAllowedOrigin()));
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(List.of(appConfigurationProvider.getJwtTokenHeader()));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(EndpointProvider.URL_ANY, configuration);

        return source;
    }
}
