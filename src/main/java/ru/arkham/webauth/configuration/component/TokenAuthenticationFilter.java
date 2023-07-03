package ru.arkham.webauth.configuration.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.arkham.webauth.service.TokenService;

import java.io.IOException;
import java.util.Optional;

/**
 * Фильтр авторизации с помощью токенов.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Сервис работы с данными пользователей модуля безопасности.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Провайдер токенов.
     */
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain chain) throws ServletException, IOException {
        Optional<String> token = tokenService.getTokenFromRequest(request);
        Optional<Jws<Claims>> claimsJws = token.flatMap(tokenService::tryParseToken);

        if (claimsJws.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String username = claimsJws.get().getBody().getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            tokenService.addTokenToResponse(token.get(), response);
        } catch (Exception exception) {
            log.error("Ошибка авторизации пользователя!", exception);
        }

        chain.doFilter(request, response);
    }
}
