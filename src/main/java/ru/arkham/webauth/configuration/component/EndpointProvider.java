package ru.arkham.webauth.configuration.component;

/**
 * Провайдер конфигурации адресов.
 */
public abstract class EndpointProvider {

    public static final String URL_ANY = "/**";

    public static final String URL_HOME = "/";
    public static final String URL_ERROR = "/error";

    public static final String URL_SECURITY = "/security";
    public static final String URL_SECURITY_LOGIN = "/login";
    public static final String URL_SECURITY_REGISTER = "/register";

    public static final String URL_USER = "/user";
    public static final String URL_USER_ALL = "/all";
    public static final String URL_USER_CURRENT = "/me";
    public static final String URL_USER_ID = "/id";
    public static final String URL_USER_ID_ARG = URL_USER_ID + "{id}";
}
