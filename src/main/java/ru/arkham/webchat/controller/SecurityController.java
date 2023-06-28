package ru.arkham.webchat.controller;

import jakarta.validation.Valid;
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
import ru.arkham.webchat.configuration.component.TokenProvider;
import ru.arkham.webchat.controller.mapper.UserMapper;
import ru.arkham.webchat.controller.request.LoginRequest;
import ru.arkham.webchat.controller.request.RegisterRequest;
import ru.arkham.webchat.controller.response.UserData;
import ru.arkham.webchat.exception.DuplicatedUserinfoException;
import ru.arkham.webchat.model.User;
import ru.arkham.webchat.service.UserService;

/**
 * Контроллер модуля безопасности.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(SecurityController.URL_HOME)
public class SecurityController {

    public static final String URL_HOME = "/security";
    public static final String URL_LOGIN = "/login";
    public static final String URL_REGISTER = "/register";

    /**
     * Сервис работы с пользователями.
     */
    private final UserService userService;

    /**
     * Менеджер авторизации.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Провайдер JWS токенов.
     */
    private final TokenProvider tokenProvider;

    /**
     * POST запрос авторизации пользователя.
     * @param loginRequest тело запроса авторизации.
     * @return тело ответа авторизации.
     */
    @PostMapping(URL_LOGIN)
    public ResponseEntity<UserData> processLogin(@Valid @RequestBody LoginRequest loginRequest) {
        User user = UserMapper.toUser(loginRequest);
        String token = authenticateAndGetToken(user.getName(), user.getPassword());
        HttpHeaders httpHeaders = new HttpHeaders();

        tokenProvider.addTokenToHttpHeaders(token, httpHeaders);

        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(UserMapper.toUserData(user));
    }

    /**
     * POST запрос регистрации пользователя.
     * @param registerRequest тело запроса регистрации.
     * @return тело ответа авторизации.
     * @throws DuplicatedUserinfoException если пользователь с указанными данными уже зарегистрирован.
     */
    @PostMapping(URL_REGISTER)
    public ResponseEntity<UserData> processRegistration(@Valid @RequestBody RegisterRequest registerRequest) throws DuplicatedUserinfoException {
        String name = registerRequest.getName();

        if (userService.hasUserByName(name)) {
            throw new DuplicatedUserinfoException("error");
        }

        // TODO: Изменить тело запроса для списка ролей.
        User user = UserMapper.toUser(registerRequest);
        user = userService.prepareNewUser(user);
        user = userService.saveUser(user);

        String token = authenticateAndGetToken(registerRequest.getName(), registerRequest.getPassword());
        HttpHeaders httpHeaders = new HttpHeaders();

        tokenProvider.addTokenToHttpHeaders(token, httpHeaders);

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
    private String authenticateAndGetToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        return tokenProvider.generateToken(authentication);
    }
}
