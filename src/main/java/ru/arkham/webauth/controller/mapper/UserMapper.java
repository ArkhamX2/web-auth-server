package ru.arkham.webauth.controller.mapper;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import ru.arkham.webauth.controller.request.LoginRequest;
import ru.arkham.webauth.controller.request.RegisterRequest;
import ru.arkham.webauth.controller.response.UserData;
import ru.arkham.webauth.model.Role;
import ru.arkham.webauth.model.User;

import java.util.List;

/**
 * Конвертер данных пользователя.
 */
@Service
public class UserMapper {

    /**
     * Получить пользователя из тела данных.
     * @param data тело данных пользователя.
     * @return пользователь.
     */
    public static User toUser(@NotNull UserData data) {
        User user = new User();

        user.setName(data.getName());

        return user;
    }

    /**
     * Получить пользователя из тела запроса.
     * @param request тело запроса авторизации.
     * @return пользователь.
     */
    public static User toUser(@NotNull LoginRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setPassword(request.getPassword());

        return user;
    }

    /**
     * Получить пользователя из тела запроса.
     * @param request тело запроса регистрации.
     * @return пользователь.
     */
    public static User toUser(@NotNull RegisterRequest request) {
        Role role = new Role();
        User user = new User();

        role.setName(request.getRoleName());

        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setRoles(List.of(role));

        return user;
    }

    /**
     * Получить тело данных пользователя из объекта.
     * @param user пользователь.
     * @return тело данных пользователя.
     */
    public static UserData toUserData(@NotNull User user) {
        UserData userData = new UserData();

        userData.setName(user.getName());

        return userData;
    }
}
