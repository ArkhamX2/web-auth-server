package ru.arkham.webchat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Пользовательская роль.
 */
@Data
@NoArgsConstructor
@Entity
public class Role
{
    /**
     * Название роли по-умолчанию.
     */
    public static final String NAME_DEFAULT = "USER";

    /**
     * Уникальный идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное название.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Список связанных пользователей.
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();
}
