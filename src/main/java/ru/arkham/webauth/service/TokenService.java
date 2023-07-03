package ru.arkham.webauth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.arkham.webauth.configuration.component.AppConfigurationProvider;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис работы с токенами.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class TokenService {

    /**
     * Провайдер конфигурации приложения.
     */
    private final AppConfigurationProvider appConfigurationProvider;

    /**
     * Сгенерировать новый токен.
     * @param authentication аутентифицированное состояние пользователя.
     * @return новый токен.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(
                        appConfigurationProvider.getJwtSecretKey().getBytes()),
                        SignatureAlgorithm.HS512)
                .setExpiration(Date.from(ZonedDateTime.now()
                        .plusSeconds(appConfigurationProvider.getJwtTokenLifetimeSeconds()).toInstant()))
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setId(UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .compact();
    }

    /**
     * Попытаться разобрать токен на части.
     * @param token токен.
     * @return разобранный на части токен или ничего.
     */
    public Optional<Jws<Claims>> tryParseToken(String token) {
        try {
            // Парсинг токена.
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(appConfigurationProvider.getJwtSecretKey().getBytes())
                    .build()
                    .parseClaimsJws(token);

            return Optional.of(jws);
        } catch (ExpiredJwtException exception) {
            log.error("Запрос на парсинг просроченного JWT: {} провалился: {}", token, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Запрос на парсинг неподдерживаемого JWT: {} провалился: {}", token, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Запрос на парсинг недействительного JWT: {} провалился: {}", token, exception.getMessage());
        } catch (SecurityException exception) {
            log.error("Запрос на парсинг JWT с недействительной сигнатурой: {} провалился: {}", token, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("Запрос на парсинг пустого JWT: {} провалился: {}", token, exception.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Получить токен из заголовка запроса.
     * @param request HTTP запрос.
     * @return токен.
     */
    public Optional<String> getTokenFromRequest(@NotNull HttpServletRequest request) {
        String token = request.getHeader(appConfigurationProvider.getJwtTokenHeader());

        if (StringUtils.hasText(token) && token.startsWith(appConfigurationProvider.getJwtTokenPrefix())) {
            // Возвращаем без префикса.
            return Optional.of(token.replace(appConfigurationProvider.getJwtTokenPrefix(), ""));
        }

        return Optional.empty();
    }

    /**
     * Вставить токен в заголовок ответа.
     * @param token токен.
     * @param response HTTP ответ.
     */
    public void addTokenToResponse(@NotNull String token, @NotNull HttpServletResponse response) {
        // Вставляем вместе с префиксом.
        token = appConfigurationProvider.getJwtTokenPrefix() + token;

        if (response.getHeaderNames().contains(appConfigurationProvider.getJwtTokenHeader())) {
            response.setHeader(appConfigurationProvider.getJwtTokenHeader(), token);
        } else {
            response.addHeader(appConfigurationProvider.getJwtTokenHeader(), token);
        }
    }

    /**
     * Вставить токен в набор HTTP заголовков.
     * @param token токен.
     * @param headers HTTP заголовки.
     */
    public void addTokenToHttpHeaders(@NotNull String token, @NotNull HttpHeaders headers) {
        // Вставляем вместе с префиксом.
        token = appConfigurationProvider.getJwtTokenPrefix() + token;

        if (headers.containsKey(appConfigurationProvider.getJwtTokenHeader())) {
            headers.set(appConfigurationProvider.getJwtTokenHeader(), token);
        } else {
            headers.add(appConfigurationProvider.getJwtTokenHeader(), token);
        }
    }
}
