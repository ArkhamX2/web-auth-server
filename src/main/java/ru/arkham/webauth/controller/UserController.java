package ru.arkham.webauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.arkham.webauth.configuration.component.EndpointProvider;
import ru.arkham.webauth.controller.payload.UserMapper;
import ru.arkham.webauth.controller.payload.response.UserData;
import ru.arkham.webauth.controller.exception.UserNotFoundException;
import ru.arkham.webauth.model.User;
import ru.arkham.webauth.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер пользователей.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(EndpointProvider.URL_USER)
public class UserController {

    /**
     * Сервис работы с пользователями.
     */
    private final UserService userService;

    /**
     * GET запрос получения всех пользователей.
     * @return список пользователей.
     */
    @GetMapping(EndpointProvider.URL_USER_ALL)
    public List<UserData> getAll() {
        List<User> userList = userService.findAll();
        List<UserData> userDataList = new ArrayList<>();

        for (User user: userList) {
            userDataList.add(UserMapper.toUserData(user));
        }

        return userDataList;
    }

    /**
     * GET запрос получения текущего пользователя.
     * @param details данные пользователя.
     * @return пользователь.
     * @throws UserNotFoundException если пользователь не найден.
     */
    @GetMapping(EndpointProvider.URL_USER_CURRENT)
    public UserData getCurrent(@AuthenticationPrincipal UserDetails details) throws UserNotFoundException {
        String name = details.getUsername();
        User user = userService
                .findUserByName(name)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        return UserMapper.toUserData(user);
    }

    /**
     * GET запрос получения пользователя по его уникальному имени.
     * @param id уникальное имя.
     * @return пользователь.
     * @throws UserNotFoundException если пользователь не найден.
     */
    @GetMapping(EndpointProvider.URL_USER_ID_ARG)
    public UserData get(@PathVariable String id) throws UserNotFoundException {
        User user = userService
                .findUserByName(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        return UserMapper.toUserData(user);
    }

    /**
     * DELETE запрос удаления пользователя по его уникальному имени.
     * @param id уникальное имя.
     * @return удаленный пользователь.
     * @throws UserNotFoundException если пользователь не найден.
     */
    @DeleteMapping(EndpointProvider.URL_USER_ID_ARG)
    public UserData delete(@PathVariable String id) throws UserNotFoundException {
        User user = userService
                .findUserByName(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        userService.deleteUser(user);

        return UserMapper.toUserData(user);
    }
}
