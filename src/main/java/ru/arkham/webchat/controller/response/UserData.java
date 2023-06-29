package ru.arkham.webchat.controller.response;

import lombok.Data;

/**
 * Тело данных пользователя.
 * Используется для транспортировки данных пользователя
 * между сервером и клиентом.
 */
@Data
public class UserData {

    /**
     * Уникальное имя пользователя.
     */
    private String name;
}
