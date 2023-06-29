package ru.arkham.webchat.configuration;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Конфигурация бинов для атрибутов ошибок.
 */
@Configuration
public class ErrorAttributesConfigurer {

    /**
     * Конфигурация атрибутов ошибок, которые будут включены в ответ
     * при возникновении какой-либо ошибки.
     * @return атрибуты ошибок.
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest request, ErrorAttributeOptions options) {
                // Включаем информацию об исключении,
                // сообщение об ошибке и ошибки привязки данных.
                return super.getErrorAttributes(request, options.including(
                        ErrorAttributeOptions.Include.EXCEPTION,
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS));
            }
        };
    }
}
