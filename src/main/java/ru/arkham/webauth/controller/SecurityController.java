package ru.arkham.webauth.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.arkham.webauth.configuration.component.EndpointProvider;
import ru.arkham.webauth.controller.exception.UserNotFoundException;
import ru.arkham.webauth.service.TokenService;
import ru.arkham.webauth.controller.payload.UserMapper;
import ru.arkham.webauth.controller.payload.request.LoginRequest;
import ru.arkham.webauth.controller.payload.request.RegisterRequest;
import ru.arkham.webauth.controller.payload.response.UserData;
import ru.arkham.webauth.controller.exception.DuplicatedUserinfoException;
import ru.arkham.webauth.model.User;
import ru.arkham.webauth.service.UserService;

/**
 * Контроллер модуля безопасности.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(EndpointProvider.URL_SECURITY)
public class SecurityController {

    /**
     * Сервис работы с пользователями.
     */
    private final UserService userService;

    /**
     * Сервис работы с токенами.
     */
    private final TokenService tokenService;

    /**
     * Менеджер авторизации.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * POST запрос авторизации пользователя.
     * @param request тело запроса авторизации.
     * @return тело ответа авторизации.
     * @throws UserNotFoundException если пользователь не найден.
     */
    @PostMapping(EndpointProvider.URL_SECURITY_LOGIN)
    public ResponseEntity<UserData> processLogin(@Valid @RequestBody LoginRequest request) throws UserNotFoundException {
        String token = authenticateAndGetToken(request.getName(), request.getPassword());
        HttpHeaders httpHeaders = new HttpHeaders();

        tokenService.addTokenToHttpHeaders(token, httpHeaders);

        User user = userService
                .findUserByName(request.getName())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(UserMapper.toUserData(user));
    }

    /**
     * POST запрос регистрации пользователя.
     * @param request тело запроса регистрации.
     * @return тело ответа авторизации.
     * @throws DuplicatedUserinfoException если пользователь с указанными данными уже зарегистрирован.
     */
    @PostMapping(EndpointProvider.URL_SECURITY_REGISTER)
    public ResponseEntity<UserData> processRegistration(@Valid @RequestBody RegisterRequest request) throws DuplicatedUserinfoException {
        String name = request.getName();

        if (userService.findUserByName(name).isPresent()) {
            throw new DuplicatedUserinfoException("Пользователь уже зарегистрирован!");
        }

        User user = UserMapper.toUser(request);
        user = userService.prepareNewUser(user);
        user = userService.saveUser(user);

        String token = authenticateAndGetToken(request.getName(), request.getPassword());
        HttpHeaders httpHeaders = new HttpHeaders();

        tokenService.addTokenToHttpHeaders(token, httpHeaders);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(httpHeaders)
                .body(UserMapper.toUserData(user));
    }

    /**
     * Авторизовать пользователя по его данным и получить токен.
     * @param username имя.
     * @param password пароль.
     * @return токен.
     */
    private String authenticateAndGetToken(@NotNull String username, @NotNull String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        return tokenService.generateToken(authentication);
    }
}
