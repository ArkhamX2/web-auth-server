package ru.arkham.webauth.controller.payload.response;

import lombok.Data;

/**
 * Тело данных пользователя.
 * Используется для транспортировки данных пользователя
 * между сервером и клиентом.
 * Не включает в себя конфиденциальную информацию.
 */
@Data
public class UserData {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Уникальное имя пользователя.
     */
    private String name;
}
