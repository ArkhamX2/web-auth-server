package ru.arkham.webauth;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiUserTest extends TestBase {

    String body = null;
    String token = null;
    String initial = null;
    {
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

            Resource resource = resourceLoader.getResource("classpath:testUser.json");
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
    @DisplayName("Create user without body")
    @Order(7)
    @Timeout(7)
    public void test7(){
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
                .body("{\"name\":\"\", \"password\":\"1234\"}")
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create user without password")
    @Order(7)
    @Timeout(7)
    public void test10(){
        given(specification)
                .basePath("/security/register")
                .body("{\"name\":\"123\", \"password\":\"\"}")
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Login without name")
    @Order(7)
    @Timeout(7)
    public void test11(){
        given(specification)
                .basePath("/security/login")
                .body("{\"name\":\"\", \"password\":\"1234\"}")
                .post()
                .then()
                .statusCode(400);
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

    @Test
    @DisplayName("My Profile")
    @Order(4)
    @Timeout(7)
    public void test4(){
        given(specification)
                .auth()
                .oauth2(token)
                .basePath("/user/me")
                .get()
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("All users")
    @Order(5)
    @Timeout(7)
    public void test5(){
        given(specification)
                .auth()
                .oauth2(token)
                .basePath("/user/all")
                .get()
                .then()
                .statusCode(200);
    }
}
