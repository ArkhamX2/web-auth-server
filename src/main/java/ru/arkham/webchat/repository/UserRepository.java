package ru.arkham.webchat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.arkham.webchat.model.User;

import java.util.Optional;

/**
 * Репозиторий данных пользователей.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Найти пользователя по его имени.
     * @param name имя.
     * @return Пользователь.
     */
    Optional<User> findByName(String name);
}
