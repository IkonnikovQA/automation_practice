package com.qa.practice.tests.api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

import com.qa.practice.api.client.BookingApi;
import com.qa.practice.api.models.AuthResponse;
import com.qa.practice.config.Config;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Epic("API")
@Feature("Health & Auth")
@Tag("api")
@Tag("regression")
public class AuthApiTest {

  private final BookingApi bookingApi = new BookingApi();

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Health check")
  @DisplayName("GET /ping returns Created")
  void ping_returnsCreated() {
    Response response = bookingApi.ping();

    assertThat(response.statusCode()).isEqualTo(201);
    assertThat(response.asString()).containsIgnoringCase("Created");
  }

  @Test
  @Tag("smoke")
  @Tag("contract")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.BLOCKER)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Create token")
  @DisplayName("POST /auth returns token for valid credentials")
  void auth_withValidCredentials_returnsToken() {
    Response response = bookingApi.createToken(Config.authUsername(), Config.authPassword());

    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/auth-success-schema.json"));
    AuthResponse auth = response.as(AuthResponse.class);
    assertThat(auth.token()).isNotBlank();
  }

  @ParameterizedTest(name = "{index}: invalid auth payload => reason Bad credentials")
  @MethodSource("invalidAuthPayloads")
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Create token")
  @DisplayName("POST /auth handles invalid credentials")
  void auth_withInvalidCredentials_returnsReason(Map<String, Object> payload) {
    Response response = bookingApi.createToken(payload);
    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/auth-failure-schema.json"));
    assertThat(response.jsonPath().getString("reason")).isEqualTo("Bad credentials");
  }

  private static Stream<Map<String, Object>> invalidAuthPayloads() {
    return Stream.of(
        Map.of("username", "wrong", "password", "wrong"),
        Map.of("username", "", "password", ""),
        Map.of("username", "admin", "password", "wrong"));
  }
}
