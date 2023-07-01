package ru.arkham.webchat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.arkham.webchat.controller.mapper.UserMapper;
import ru.arkham.webchat.controller.response.UserData;
import ru.arkham.webchat.exception.UserNotFoundException;
import ru.arkham.webchat.model.User;
import ru.arkham.webchat.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер пользователей.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(UserController.URL_HOME)
public class UserController {

    public static final String URL_HOME = "/user";
    public static final String URL_ALL = "/all";
    public static final String URL_CURRENT = "/me";
    public static final String URL_ID = "/id";

    /**
     * Сервис работы с пользователями.
     */
    private final UserService userService;

    /**
     * GET запрос получения всех пользователей.
     * @return список пользователей.
     */
    @GetMapping(URL_ALL)
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
    @GetMapping(URL_CURRENT)
    public UserData getCurrent(@AuthenticationPrincipal UserDetails details) throws UserNotFoundException {
        String name = details.getUsername();
        User user = userService
                .findUserByName(name)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        return UserMapper.toUserData(user);
    }

    /**
     * GET запрос получения пользователя по его уникальному имени.
     * @param name уникальное имя.
     * @return пользователь.
     * @throws UserNotFoundException если пользователь не найден.
     */
    @GetMapping(URL_ID + "/{name}")
    public UserData get(@PathVariable String name) throws UserNotFoundException {
        User user = userService
                .findUserByName(name)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        return UserMapper.toUserData(user);
    }

    /**
     * DELETE запрос удаления пользователя по его уникальному имени.
     * @param name уникальное имя.
     * @return удаленный пользователь.
     * @throws UserNotFoundException если пользователь не найден.
     */
    @DeleteMapping(URL_ID + "/{name}")
    public UserData deleteUser(@PathVariable String name) throws UserNotFoundException {
        User user = userService
                .findUserByName(name)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        userService.deleteUser(user);

        return UserMapper.toUserData(user);
    }
}
