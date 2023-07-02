# Этап сборки.
FROM eclipse-temurin:17-jdk-jammy AS build

# Переключение в директорию проекта.
WORKDIR /app

# Копирование исходного кода проекта в контейнер.
COPY . .

# Очистка директории сборки.
RUN rm -rf ./build/libs

# Сборка проекта.
RUN ./gradlew bootjar -i --stacktrace --no-daemon

# Этап запуска.
# TODO: Использовать eclipse-temurin:17-jre-jammy для запуска на боевом сервере.
FROM eclipse-temurin:17-jdk-jammy

# Рабочая дирректория.
WORKDIR /app

# Копирование билда проекта.
COPY --from=build /app/build/libs/*.jar ./web-server.jar

# Переменные окружения для настройки подключения к базе данных.
ENV DATASOURCE_DATABASE="web-chat"
ENV DATASOURCE_HOST="localhost"
ENV DATASOURCE_PORT="3306"
ENV DATASOURCE_USER="arkham"
ENV DATASOURCE_PASSWORD="secret"

# Переменная окружения параметров запуска Java для удаленной отладки.
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Открытие портов 8080 (tomcat) и 5005 (debugger).
EXPOSE 8080 5005

# Запуск. Используется встроенный сервер tomcat.
ENTRYPOINT ["java", "-jar", "web-server.jar"]
