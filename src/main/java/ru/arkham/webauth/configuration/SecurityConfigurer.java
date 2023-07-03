package ru.arkham.webauth.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.arkham.webauth.configuration.component.EndpointProvider;
import ru.arkham.webauth.configuration.component.TokenAuthenticationFilter;

/**
 * Конфигуратор бинов для модуля безопасности.
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfigurer {

    /**
     * Фильтр авторизации с помощью токенов.
     */
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * Менеджер авторизации.
     * @param configuration конфигурация авторизации.
     * @return менеджер авторизации.
     * @throws Exception при внутренней ошибке.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Фильтр доступа.
     * @param http строитель настроек безопасности.
     * @return фильтр доступа.
     * @throws Exception при внутренней ошибке.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: Добавить фильтрацию по ролям.
        http.authorizeHttpRequests(registry -> registry
                .requestMatchers(EndpointProvider.URL_HOME).permitAll()
                .requestMatchers(EndpointProvider.URL_ERROR).permitAll()
                .requestMatchers(EndpointProvider.URL_SECURITY + EndpointProvider.URL_ANY).permitAll()
                .anyRequest().authenticated());
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(configurer -> configurer
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.sessionManagement(configurer -> configurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Сервис шифрования.
     * @return сервис шифрования.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
