package com.qa.practice.tests.api;

import com.qa.practice.api.client.BookingApi;
import com.qa.practice.api.models.AuthResponse;
import com.qa.practice.config.Config;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Health & Auth")
@Tag("api")
@Tag("smoke")
public class AuthApiTest {

    private final BookingApi bookingApi = new BookingApi();

    @Test
    @Story("Health check")
    @DisplayName("GET /ping returns Created")
    void ping_returnsCreated() {
        Response response = bookingApi.ping();

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.asString()).containsIgnoringCase("Created");
    }

    @Test
    @Story("Create token")
    @DisplayName("POST /auth returns token for valid credentials")
    void auth_withValidCredentials_returnsToken() {
        Response response = bookingApi.createToken(Config.authUsername(), Config.authPassword());

        assertThat(response.statusCode()).isEqualTo(200);
        AuthResponse auth = response.as(AuthResponse.class);
        assertThat(auth.token()).isNotBlank();
    }

    @Test
    @Story("Create token")
    @DisplayName("POST /auth fails for invalid credentials")
    void auth_withInvalidCredentials_returnsReason() {
        Response response = bookingApi.createToken("wrong", "wrong");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("reason")).isEqualTo("Bad credentials");
    }

    @Test
    @Story("Create token")
    @DisplayName("POST /auth with empty credentials returns bad credentials reason")
    void auth_withEmptyCredentials_returnsBadCredentialsReason() {
        Response response = bookingApi.createToken(Map.of("username", "", "password", ""));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("reason")).isEqualTo("Bad credentials");
    }
}
