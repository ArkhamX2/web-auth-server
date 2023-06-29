package ru.arkham.webchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.arkham.webchat.model.Role;
import ru.arkham.webchat.model.User;
import ru.arkham.webchat.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Сервис работы с данными пользователей модуля безопасности.
 */
@RequiredArgsConstructor
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    /**
     * Сервис работы с пользователями.
     */
    private final UserRepository userRepository;

    /**
     * Получить данные пользователя модуля безопасности по его имени.
     * @param username имя пользователя.
     * @return данные пользователя.
     * @throws UsernameNotFoundException если пользователь не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден!"));

        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    /**
     * Получить разрешения на основе пользовательских ролей.
     * @param roles пользовательские роли.
     * @return разрешения.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection <Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
