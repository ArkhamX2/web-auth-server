package ru.arkham.webauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.arkham.webauth.model.Role;

import java.util.Optional;

/**
 * Репозиторий данных пользователей.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Найти пользовательскую роль по ее названию.
     * @param name название.
     * @return Пользовательская роль.
     */
    Optional<Role> findByName(String name);
}
