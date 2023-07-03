package ru.arkham.webauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Сервер авторизации.
 */
@SpringBootApplication
public class WebAuthServerApplication {

    /**
     * Входная точка запуска.
     * @param args аргументы.
     */
    public static void main(String[] args) {
        SpringApplication.run(WebAuthServerApplication.class, args);
    }
}
