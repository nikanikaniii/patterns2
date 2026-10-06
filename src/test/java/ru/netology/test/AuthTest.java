package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.data.RegistrationDto;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static groovy.xml.dom.DOMCategory.text;

class AuthTest {

    private static final Duration WAIT = Duration.ofSeconds(15);

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    private void login(String login, String password) {
        $("[data-test-id=login] input").setValue(login);
        $("[data-test-id=password] input").setValue(password);
        $("button.button").click();
    }

    private void shouldSeeError(String message) {
        $("[data-test-id=error-notification] .notification__content")
                .shouldBe(visible, WAIT)
                .shouldHave(text(message));
    }

    @Test
    @DisplayName("Активный зарегистрированный пользователь входит в личный кабинет")
    void shouldLoginActiveRegisteredUser() {
        RegistrationDto user = DataGenerator.getRegisteredUser("active");
        login(user.getLogin(), user.getPassword());
        $("h2").shouldBe(visible, WAIT).shouldHave(text("Личный кабинет"));
    }

    @Test
    @DisplayName("Незарегистрированный пользователь получает ошибку")
    void shouldGetErrorIfNotRegisteredUser() {
        RegistrationDto user = DataGenerator.getUser("active");
        login(user.getLogin(), user.getPassword());
        shouldSeeError("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Заблокированный пользователь получает ошибку")
    void shouldGetErrorIfBlockedUser() {
        RegistrationDto user = DataGenerator.getRegisteredUser("blocked");
        login(user.getLogin(), user.getPassword());
        shouldSeeError("Ошибка! Пользователь заблокирован");
    }

    @Test
    @DisplayName("Неверный логин при верном пароле — ошибка")
    void shouldGetErrorIfWrongLogin() {
        RegistrationDto user = DataGenerator.getRegisteredUser("active");
        login(DataGenerator.getRandomLogin(), user.getPassword());
        shouldSeeError("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Неверный пароль при верном логине — ошибка")
    void shouldGetErrorIfWrongPassword() {
        RegistrationDto user = DataGenerator.getRegisteredUser("active");
        login(user.getLogin(), DataGenerator.getRandomPassword());
        shouldSeeError("Ошибка! Неверно указан логин или пароль");
    }
}
