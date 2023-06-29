package ru.arkham.webchat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Пользователь.
 */
@Data
@NoArgsConstructor
@Entity
public class User {

    /**
     * Уникальный идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное имя.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Пароль.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Список связанных пользовательских ролей.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable
    private List<Role> roles = new ArrayList<>();
}
