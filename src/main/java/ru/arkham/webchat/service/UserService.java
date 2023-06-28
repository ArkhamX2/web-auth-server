package ru.arkham.webchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.arkham.webchat.model.Role;
import ru.arkham.webchat.model.User;
import ru.arkham.webchat.repository.RoleRepository;
import ru.arkham.webchat.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.arkham.webchat.model.Role.NAME_DEFAULT;

/**
 * Сервис работы с пользователями.
 */
@RequiredArgsConstructor
@Service
public class UserService {

    /**
     * Репозиторий пользователей.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий пользовательских ролей.
     */
    private final RoleRepository roleRepository;

    /**
     * Шифратор паролей.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Подготовить данные нового пользователя для сохранения.
     * @param user пользователь.
     * @return готовый пользователь.
     */
    public User prepareNewUser(User user) {
        List<String> roleNames = new ArrayList<>();

        user.getRoles().forEach(x -> roleNames.add(x.getName()));

        if (!roleNames.contains(NAME_DEFAULT)) {
            roleNames.add(NAME_DEFAULT);
        }

        user.setPassword(encodePassword(user.getPassword()));
        user.setRoles(createRoles(roleNames));

        return user;
    }

    /**
     * Сохранить пользователя.
     * @param user пользователь.
     * @return сохраненный пользователь.
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Получить пользователя по имени.
     * @param name имя.
     * @return пользователь.
     */
    public Optional<User> findUserByName(String name) {
        return userRepository.findByName(name);
    }

    /**
     * Проверить наличие пользователя по его имени.
     * @param name имя.
     * @return статус проверки.
     */
    public Boolean hasUserByName(String name) {
        return findUserByName(name).isPresent();
    }

    /**
     * Получить хеш пароля.
     * @param rawPassword пароль.
     * @return хеш пароля.
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Создать и получить пользовательские роли.
     * @param roleNames названия ролей.
     * @return пользовательские роли.
     */
    public List<Role> createRoles(List<String> roleNames) {
        List<Role> roles = new ArrayList<>();

        for (String roleName: roleNames) {
            Optional<Role> role = roleRepository.findByName(roleName);

            if (role.isEmpty()) {
                roles.add(createRole(roleName));
                continue;
            }

            roles.add(role.get());
        }

        return roles;
    }

    /**
     * Создать и получить пользовательскую роль.
     * @param name название роли.
     * @return пользовательская роль.
     */
    private Role createRole(String name) {
        Role role = new Role();

        role.setName(name);

        return roleRepository.save(role);
    }
}
