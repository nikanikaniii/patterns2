package ru.netology.data;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import net.datafaker.Faker;

import java.util.Locale;

import static io.restassured.RestAssured.given;

@UtilityClass
public class DataGenerator {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(io.restassured.filter.log.LogDetail.ALL)
            .build();

    private static final Faker faker = new Faker(new Locale("en"));

    public static String getRandomLogin() {
        return faker.name().username();
    }

    public static String getRandomPassword() {
        return faker.internet().password();
    }

    private static void sendRequest(RegistrationDto user) {
        given()
                .spec(requestSpec)
                .body(new Gson().toJson(user))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    public static RegistrationDto getUser(String status) {
        return new RegistrationDto(getRandomLogin(), getRandomPassword(), status);
    }

    public static RegistrationDto getRegisteredUser(String status) {
        var registeredUser = getUser(status);
        sendRequest(registeredUser);
        return registeredUser;
    }
}
