package ru.arkham.webauth;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static io.restassured.RestAssured.given;
import static ru.arkham.webauth.TestBase.specification;

public class ApiAdminTest {

    private String body = null;
    private String token = null;
    private String initial = null;
    {
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

            Resource resource = resourceLoader.getResource("classpath:testAdmin.json");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            initial = stringBuilder.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject(initial);
        body = JSONObject.valueToString(object);

        TestBase.BeforeEach();
    }

    @Test
    @DisplayName("Create user without body")
    @Order(7)
    @Timeout(7)
    public void test7() {
        given(specification)
                .basePath("/security/register")
                .body("")
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create user without name")
    @Order(7)
    @Timeout(7)
    public void test8(){
        given(specification)
                .basePath("/security/register")
                .body("{\"name\":\"\", \"password\":\"1234\", \"roleName\":\"ADMIN\"}")
                .post()
                .then()
                .statusCode(400)
        ;

    }
    @Test
    @DisplayName("Create user without password")
    @Order(7)
    @Timeout(7)
    public void test10(){
        given(specification)
                .basePath("/security/register")
                .body("{\"name\":\"123\", \"password\":\"\", \"roleName\":\"ADMIN\"}")
                .post()
                .then()
                .statusCode(400)
        ;

    }
    @Test
    @DisplayName("Login without name")
    @Order(7)
    @Timeout(7)
    public void test11(){
        given(specification)
                .basePath("/security/login")
                .body("{\"name\":\"\", \"password\":\"1234\", \"roleName\":\"ADMIN\"}")
                .post()
                .then()
                .statusCode(400)
        ;

    }
    @Test
    @DisplayName("Login without password")
    @Order(7)
    @Timeout(7)
    public void test9(){
        given(specification)
                .basePath("/security/login")
                .body("{\"name\":\"123\", \"password\":\"\"}")
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create user")
    @Order(1)
    @Timeout(7)
    public void test1(){
        given(specification)
                .basePath("/security/register")
                .body(body)
                .post()
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Log In")
    @Order(2)
    @Timeout(7)
    public void test2(){
        token = given(specification)
                .basePath("/security/login")
                .body(body)
                .post()
                .then()
                .statusCode(200)
                .extract()
                .headers()
                .getValue("Authorization").substring(7);
    }
}
