package ru.arkham.webauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.arkham.webauth.configuration.component.AppConfigurationProvider;
import ru.arkham.webauth.model.Role;
import ru.arkham.webauth.model.User;
import ru.arkham.webauth.repository.RoleRepository;
import ru.arkham.webauth.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        if (!roleNames.contains(AppConfigurationProvider.ROLE_NAME_DEFAULT)) {
            roleNames.add(AppConfigurationProvider.ROLE_NAME_DEFAULT);
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
     * Получить всех пользователей.
     * @return список пользователей.
     */
    public List<User> findAll() {
        return userRepository.findAll();
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
     * Удалить пользователя.
     * @param user пользователь.
     * @return статус успеха операции.
     */
    public Boolean deleteUser(User user) {
        userRepository.delete(user);

        try {
            userRepository.delete(user);
        } catch (OptimisticLockingFailureException exception) {
            return false;
        }

        return true;
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
