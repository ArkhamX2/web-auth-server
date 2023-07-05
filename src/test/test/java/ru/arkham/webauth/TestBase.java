package ru.arkham.webauth;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

    public static RequestSpecification specification;

    public static void BeforeEach() {
        specification = new RequestSpecBuilder()
                .addHeader("Accept"," application/json, text/plain, */*")
                .addHeader("Content-Type","application/json;;charset=UTF-8")
                .addHeader("Accept-Encoding","gzip,deflate,br")
                .addHeader("Host","localhost:8080")
                .setBaseUri(("http://localhost"))
                .build();
    }
}
