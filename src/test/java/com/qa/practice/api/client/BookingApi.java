package com.qa.practice.api.client;

import static io.restassured.RestAssured.given;

import com.qa.practice.api.models.AuthRequest;
import com.qa.practice.api.models.AuthResponse;
import com.qa.practice.api.models.Booking;
import com.qa.practice.api.models.BookingResponse;
import com.qa.practice.api.specs.RequestSpecs;
import com.qa.practice.config.Config;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class BookingApi {

  @Step("GET /ping")
  public Response ping() {
    return given().spec(RequestSpecs.base()).when().get("/ping").then().extract().response();
  }

  @Step("POST /auth")
  public Response createToken(String username, String password) {
    return given()
        .spec(RequestSpecs.base())
        .body(new AuthRequest(username, password))
        .when()
        .post("/auth")
        .then()
        .extract()
        .response();
  }

  @Step("POST /auth с произвольным payload")
  public Response createToken(Object body) {
    return given()
        .spec(RequestSpecs.base())
        .body(body)
        .when()
        .post("/auth")
        .then()
        .extract()
        .response();
  }

  @Step("Создать auth token с credentials по умолчанию")
  public String createToken() {
    AuthResponse auth =
        createToken(Config.authUsername(), Config.authPassword())
            .then()
            .statusCode(200)
            .extract()
            .as(AuthResponse.class);
    return auth.token();
  }

  @Step("GET /booking")
  public Response getBookingIds() {
    return given().spec(RequestSpecs.base()).when().get("/booking").then().extract().response();
  }

  @Step("GET /booking с фильтром firstname и lastname")
  public Response getBookingIdsByName(String firstName, String lastName) {
    return given()
        .spec(RequestSpecs.base())
        .queryParam("firstname", firstName)
        .queryParam("lastname", lastName)
        .when()
        .get("/booking")
        .then()
        .extract()
        .response();
  }

  @Step("GET /booking с фильтром firstname")
  public Response getBookingIdsByFirstName(String firstName) {
    return given()
        .spec(RequestSpecs.base())
        .queryParam("firstname", firstName)
        .when()
        .get("/booking")
        .then()
        .extract()
        .response();
  }

  @Step("GET /booking с фильтром lastname")
  public Response getBookingIdsByLastName(String lastName) {
    return given()
        .spec(RequestSpecs.base())
        .queryParam("lastname", lastName)
        .when()
        .get("/booking")
        .then()
        .extract()
        .response();
  }

  @Step("GET /booking/{bookingId}")
  public Response getBooking(int bookingId) {
    return given()
        .spec(RequestSpecs.base())
        .when()
        .get("/booking/{id}", bookingId)
        .then()
        .extract()
        .response();
  }

  @Step("POST /booking")
  public Response createBooking(Booking booking) {
    return given()
        .spec(RequestSpecs.base())
        .body(booking)
        .when()
        .post("/booking")
        .then()
        .extract()
        .response();
  }

  @Step("POST /booking с произвольным payload")
  public Response createBooking(Object payload) {
    return given()
        .spec(RequestSpecs.base())
        .body(payload)
        .when()
        .post("/booking")
        .then()
        .extract()
        .response();
  }

  @Step("POST /booking с raw body и Content-Type")
  public Response createBookingRaw(String rawBody, String contentType) {
    return given()
        .spec(RequestSpecs.base())
        .contentType(contentType)
        .body(rawBody)
        .when()
        .post("/booking")
        .then()
        .extract()
        .response();
  }

  @Step("PUT /booking/{bookingId}")
  public Response updateBooking(int bookingId, Booking booking, String token) {
    return given()
        .spec(RequestSpecs.withToken(token))
        .body(booking)
        .when()
        .put("/booking/{id}", bookingId)
        .then()
        .extract()
        .response();
  }

  @Step("PUT /booking/{bookingId} без auth token")
  public Response updateBookingWithoutToken(int bookingId, Booking booking) {
    return given()
        .spec(RequestSpecs.base())
        .body(booking)
        .when()
        .put("/booking/{id}", bookingId)
        .then()
        .extract()
        .response();
  }

  @Step("PATCH /booking/{bookingId}")
  public Response partialUpdateBooking(int bookingId, Object patchBody, String token) {
    return given()
        .spec(RequestSpecs.withToken(token))
        .body(patchBody)
        .when()
        .patch("/booking/{id}", bookingId)
        .then()
        .extract()
        .response();
  }

  @Step("PATCH /booking/{bookingId} без auth token")
  public Response partialUpdateBookingWithoutToken(int bookingId, Object patchBody) {
    return given()
        .spec(RequestSpecs.base())
        .body(patchBody)
        .when()
        .patch("/booking/{id}", bookingId)
        .then()
        .extract()
        .response();
  }

  @Step("DELETE /booking/{bookingId}")
  public Response deleteBooking(int bookingId, String token) {
    return given()
        .spec(RequestSpecs.withToken(token))
        .when()
        .delete("/booking/{id}", bookingId)
        .then()
        .extract()
        .response();
  }

  @Step("DELETE /booking/{bookingId} без auth token")
  public Response deleteBookingWithoutToken(int bookingId) {
    return given()
        .spec(RequestSpecs.base())
        .when()
        .delete("/booking/{id}", bookingId)
        .then()
        .extract()
        .response();
  }

  @Step("Разобрать BookingResponse")
  public BookingResponse asBookingResponse(Response response) {
    return response.as(BookingResponse.class);
  }

  @Step("Разобрать Booking")
  public Booking asBooking(Response response) {
    return response.as(Booking.class);
  }
}
