package ru.arkham.webchat.configuration.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Провайдер токенов.
 */
@Slf4j
@Component
public class TokenProvider {

    /**
     * Заголовок токена в заголовке HTTP запроса.
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Префикс токена в заголовке HTTP запроса.
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Подписывающий ключ.
     * Используется алгоритм HS512.
     */
    @Value("${app.jwt.secret-key}")
    private String secretKey;

    /**
     * Жизненный цикл токена в секундах.
     */
    @Value("${app.jwt.token-lifetime-seconds}")
    private Long tokenLifetimeSeconds;

    /**
     * Сгенерировать новый токен.
     * @param authentication аутентифицированное состояние пользователя.
     * @return новый токен.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS512)
                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(tokenLifetimeSeconds).toInstant()))
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
                    .setSigningKey(secretKey.getBytes())
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
        String token = request.getHeader(TOKEN_HEADER);

        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            // Возвращаем без префикса.
            return Optional.of(token.replace(TOKEN_PREFIX, ""));
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
        token = TOKEN_PREFIX + token;

        if (response.getHeaderNames().contains(TOKEN_HEADER)) {
            response.setHeader(TOKEN_HEADER, token);
        } else {
            response.addHeader(TOKEN_HEADER, token);
        }
    }

    /**
     * Вставить токен в набор HTTP заголовков.
     * @param token токен.
     * @param headers HTTP заголовки.
     */
    public void addTokenToHttpHeaders(@NotNull String token, @NotNull HttpHeaders headers) {
        // Вставляем вместе с префиксом.
        token = TOKEN_PREFIX + token;

        if (headers.containsKey(TOKEN_HEADER)) {
            headers.set(TOKEN_HEADER, token);
        } else {
            headers.add(TOKEN_HEADER, token);
        }
    }
}
