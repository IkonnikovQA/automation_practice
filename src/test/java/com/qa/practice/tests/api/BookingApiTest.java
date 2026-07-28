package com.qa.practice.tests.api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

import com.qa.practice.api.client.BookingApi;
import com.qa.practice.api.models.Booking;
import com.qa.practice.api.models.BookingDates;
import com.qa.practice.api.models.BookingResponse;
import com.qa.practice.data.TestDataFactory;
import com.qa.practice.data.builders.BookingBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("API")
@Feature("Бронирования")
@Tag("api")
@Tag("regression")
public class BookingApiTest {

  private final BookingApi bookingApi = new BookingApi();
  private final List<Integer> createdBookingIds = new ArrayList<>();
  private String token;

  @AfterEach
  void cleanupCreatedBookings() {
    if (createdBookingIds.isEmpty()) {
      return;
    }
    String authToken = token != null ? token : bookingApi.createToken();
    for (Integer bookingId : createdBookingIds) {
      Response response = bookingApi.deleteBooking(bookingId, authToken);
      if (response.statusCode() != 201 && response.statusCode() != 404) {
        throw new AssertionError(
            "Очистка не удалась для bookingId=" + bookingId + ", status=" + response.statusCode());
      }
    }
    createdBookingIds.clear();
  }

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Список бронирований")
  @DisplayName("GET /booking возвращает список id бронирований")
  void getBookingIds_returnsList() {
    Response response = bookingApi.getBookingIds();

    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/booking-id-list-schema.json"));
    List<Map<String, Integer>> ids = response.jsonPath().getList("$");
    assertThat(ids).isNotEmpty();
    assertThat(ids.get(0)).containsKey("bookingid");
  }

  @Test
  @Tag("smoke")
  @Tag("contract")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.BLOCKER)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Создание бронирования")
  @DisplayName("POST /booking создаёт бронирование и возвращает id")
  void createBooking_returnsIdAndBody() {
    Booking payload = TestDataFactory.randomBooking();

    Response response = bookingApi.createBooking(payload);

    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/booking-create-response-schema.json"));
    BookingResponse created = bookingApi.asBookingResponse(response);
    createdBookingIds.add(created.bookingid());
    assertThat(created.bookingid()).isPositive();
    assertThat(created.booking().firstname()).isEqualTo(payload.firstname());
    assertThat(created.booking().lastname()).isEqualTo(payload.lastname());
    assertThat(created.booking().totalprice()).isEqualTo(payload.totalprice());
    assertThat(created.booking().bookingdates().checkin())
        .isEqualTo(payload.bookingdates().checkin());
  }

  @Test
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Создание бронирования")
  @DisplayName("POST /booking поддерживает null в additionalneeds")
  void createBooking_withNullAdditionalNeeds_returnsCreatedBooking() {
    Booking payload = BookingBuilder.random().withAdditionalneeds(null).build();

    Response response = bookingApi.createBooking(payload);

    assertThat(response.statusCode()).isEqualTo(200);
    BookingResponse created = bookingApi.asBookingResponse(response);
    createdBookingIds.add(created.bookingid());
    assertThat(created.bookingid()).isPositive();
    assertThat(created.booking().firstname()).isEqualTo(payload.firstname());
    assertThat(created.booking().lastname()).isEqualTo(payload.lastname());
    assertThat(created.booking().additionalneeds()).isNull();
  }

  @Test
  @Tag("regression")
  @Tag("contract")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Создание бронирования")
  @DisplayName("POST /booking поддерживает totalprice = 0")
  void createBooking_withZeroTotalPrice_returnsCreatedBooking() {
    Booking payload = BookingBuilder.random().withTotalprice(0).build();

    Response response = bookingApi.createBooking(payload);

    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/booking-create-response-schema.json"));
    BookingResponse created = bookingApi.asBookingResponse(response);
    createdBookingIds.add(created.bookingid());
    assertThat(created.bookingid()).isPositive();
    assertThat(created.booking().totalprice()).isZero();
    assertThat(created.booking().firstname()).isEqualTo(payload.firstname());
    assertThat(created.booking().lastname()).isEqualTo(payload.lastname());
  }

  @Test
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Создание бронирования")
  @DisplayName("POST /booking поддерживает depositpaid=false")
  void createBooking_withDepositNotPaid_returnsCreatedBooking() {
    Booking payload = BookingBuilder.random().withDepositpaid(false).build();

    Response response = bookingApi.createBooking(payload);

    assertThat(response.statusCode()).isEqualTo(200);
    BookingResponse created = bookingApi.asBookingResponse(response);
    createdBookingIds.add(created.bookingid());
    assertThat(created.booking().depositpaid()).isFalse();

    Response refetchResponse = bookingApi.getBooking(created.bookingid());
    assertThat(refetchResponse.statusCode()).isEqualTo(200);
    assertThat(bookingApi.asBooking(refetchResponse).depositpaid()).isFalse();
  }

  @Test
  @Tag("smoke")
  @Tag("contract")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Получение бронирования по id")
  @DisplayName("GET /booking/{id} возвращает созданное бронирование")
  void getBookingById_returnsCreatedBooking() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);

    Response response = bookingApi.getBooking(bookingId);

    assertThat(response.statusCode()).isEqualTo(200);
    response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    Booking booking = bookingApi.asBooking(response);
    assertThat(booking.firstname()).isEqualTo(payload.firstname());
    assertThat(booking.lastname()).isEqualTo(payload.lastname());
    assertThat(booking.additionalneeds()).isEqualTo(payload.additionalneeds());
  }

  @Test
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Список бронирований")
  @DisplayName("GET /booking поддерживает фильтры firstname и lastname")
  void getBookingIds_withNameFilters_containsCreatedBookingId() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    Booking payload =
        new Booking(
            "Auto" + suffix,
            "Case" + suffix,
            111,
            true,
            new BookingDates("2026-08-20", "2026-08-22"),
            "Breakfast");
    int bookingId = createAndTrackBooking(payload);

    Response response = bookingApi.getBookingIdsByName(payload.firstname(), payload.lastname());
    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/booking-id-list-schema.json"));
    List<Map<String, Integer>> ids = response.jsonPath().getList("$");
    assertThat(ids).isNotEmpty();
    assertThat(ids).anyMatch(item -> item.get("bookingid") == bookingId);
  }

  @Test
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Список бронирований")
  @DisplayName("GET /booking поддерживает фильтр только по firstname")
  void getBookingIds_withFirstNameFilter_containsCreatedBookingId() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    Booking payload =
        new Booking(
            "AutoFirst" + suffix,
            "Case" + suffix,
            111,
            true,
            new BookingDates("2026-08-20", "2026-08-22"),
            "Breakfast");
    int bookingId = createAndTrackBooking(payload);

    Response response = bookingApi.getBookingIdsByFirstName(payload.firstname());
    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/booking-id-list-schema.json"));
    List<Map<String, Integer>> ids = response.jsonPath().getList("$");
    assertThat(ids).isNotEmpty();
    assertThat(ids).anyMatch(item -> item.get("bookingid") == bookingId);
  }

  @Test
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Список бронирований")
  @DisplayName("GET /booking поддерживает фильтр только по lastname")
  void getBookingIds_withLastNameFilter_containsCreatedBookingId() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    Booking payload =
        new Booking(
            "AutoLast" + suffix,
            "CaseLast" + suffix,
            111,
            true,
            new BookingDates("2026-08-20", "2026-08-22"),
            "Breakfast");
    int bookingId = createAndTrackBooking(payload);

    Response response = bookingApi.getBookingIdsByLastName(payload.lastname());
    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/booking-id-list-schema.json"));
    List<Map<String, Integer>> ids = response.jsonPath().getList("$");
    assertThat(ids).isNotEmpty();
    assertThat(ids).anyMatch(item -> item.get("bookingid") == bookingId);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Список бронирований")
  @DisplayName("GET /booking с неизвестными фильтрами возвращает пустой список")
  void getBookingIds_withUnknownNameFilters_returnsEmptyList() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    Response response =
        bookingApi.getBookingIdsByName("NoSuchFirst" + suffix, "NoSuchLast" + suffix);
    assertThat(response.statusCode()).isEqualTo(200);
    response
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/booking-id-list-schema.json"));
    List<Map<String, Integer>> ids = response.jsonPath().getList("$");
    assertThat(ids).isEmpty();
  }

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Обновление бронирования")
  @DisplayName("PUT /booking/{id} обновляет бронирование с токеном")
  void updateBooking_withToken_updatesFields() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);
    token = bookingApi.createToken();

    Booking updated =
        new Booking(
            "Updated",
            "Name",
            999,
            false,
            new BookingDates(payload.bookingdates().checkin(), payload.bookingdates().checkout()),
            "Late checkout");

    Response response = bookingApi.updateBooking(bookingId, updated, this.token);

    assertThat(response.statusCode()).isEqualTo(200);
    Booking body = bookingApi.asBooking(response);
    assertThat(body.firstname()).isEqualTo("Updated");
    assertThat(body.lastname()).isEqualTo("Name");
    assertThat(body.totalprice()).isEqualTo(999);
    assertThat(body.additionalneeds()).isEqualTo("Late checkout");

    Response refetchResponse = bookingApi.getBooking(bookingId);
    assertThat(refetchResponse.statusCode()).isEqualTo(200);
    Booking refetched = bookingApi.asBooking(refetchResponse);
    assertThat(refetched.firstname()).isEqualTo("Updated");
    assertThat(refetched.totalprice()).isEqualTo(999);
  }

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Частичное обновление бронирования")
  @DisplayName("PATCH /booking/{id} обновляет только firstname")
  void partialUpdateBooking_updatesFirstname() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);
    token = bookingApi.createToken();

    Response response =
        bookingApi.partialUpdateBooking(bookingId, Map.of("firstname", "Patched"), this.token);

    assertThat(response.statusCode()).isEqualTo(200);
    Booking body = bookingApi.asBooking(response);
    assertThat(body.firstname()).isEqualTo("Patched");
    assertThat(body.lastname()).isEqualTo(payload.lastname());

    Response refetchResponse = bookingApi.getBooking(bookingId);
    assertThat(refetchResponse.statusCode()).isEqualTo(200);
    Booking refetched = bookingApi.asBooking(refetchResponse);
    assertThat(refetched.firstname()).isEqualTo("Patched");
    assertThat(refetched.lastname()).isEqualTo(payload.lastname());
  }

  @Test
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Частичное обновление бронирования")
  @DisplayName("PATCH /booking/{id} обновляет несколько полей")
  void partialUpdateBooking_updatesMultipleFields() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);
    token = bookingApi.createToken();

    Response response =
        bookingApi.partialUpdateBooking(
            bookingId,
            Map.of("lastname", "MultiPatch", "totalprice", 777, "depositpaid", false),
            this.token);

    assertThat(response.statusCode()).isEqualTo(200);
    Booking body = bookingApi.asBooking(response);
    assertThat(body.firstname()).isEqualTo(payload.firstname());
    assertThat(body.lastname()).isEqualTo("MultiPatch");
    assertThat(body.totalprice()).isEqualTo(777);
    assertThat(body.depositpaid()).isFalse();

    Response refetchResponse = bookingApi.getBooking(bookingId);
    assertThat(refetchResponse.statusCode()).isEqualTo(200);
    Booking refetched = bookingApi.asBooking(refetchResponse);
    assertThat(refetched.lastname()).isEqualTo("MultiPatch");
    assertThat(refetched.totalprice()).isEqualTo(777);
    assertThat(refetched.depositpaid()).isFalse();
  }

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Удаление бронирования")
  @DisplayName("DELETE /booking/{id} удаляет бронирование")
  void deleteBooking_removesBooking() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);
    token = bookingApi.createToken();

    Response deleteResponse = bookingApi.deleteBooking(bookingId, this.token);
    assertThat(deleteResponse.statusCode()).isEqualTo(201);
    createdBookingIds.remove(Integer.valueOf(bookingId));

    Response getResponse = bookingApi.getBooking(bookingId);
    assertThat(getResponse.statusCode()).isEqualTo(404);
  }

  @Test
  @Tag("regression")
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Удаление бронирования")
  @DisplayName("DELETE /booking/{id} повторно возвращает not found или method not allowed")
  void deleteBooking_twice_returnsClientError() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);
    token = bookingApi.createToken();

    Response firstDelete = bookingApi.deleteBooking(bookingId, this.token);
    assertThat(firstDelete.statusCode()).isEqualTo(201);
    createdBookingIds.remove(Integer.valueOf(bookingId));

    Response secondDelete = bookingApi.deleteBooking(bookingId, this.token);
    assertThat(secondDelete.statusCode()).isIn(404, 405);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Удаление бронирования")
  @DisplayName("DELETE /booking/{id} для несуществующего id возвращает client error")
  void deleteBooking_withNonExistentId_returnsClientError() {
    token = bookingApi.createToken();

    Response response = bookingApi.deleteBooking(999_999_999, this.token);
    assertThat(response.statusCode()).isIn(404, 405);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Создание бронирования")
  @DisplayName("POST /booking с пустым телом не создаёт бронирование")
  void createBooking_withEmptyBody_returnsClientOrServerError() {
    Response response = bookingApi.createBookingRaw(" ", "application/json");

    assertThat(response.statusCode()).isGreaterThanOrEqualTo(400);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Создание бронирования")
  @DisplayName("POST /booking с битым JSON не создаёт бронирование")
  void createBooking_withMalformedJson_returnsClientOrServerError() {
    Response response = bookingApi.createBookingRaw("{not-valid-json", "application/json");

    assertThat(response.statusCode()).isGreaterThanOrEqualTo(400);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Создание бронирования")
  @DisplayName("POST /booking с неверным Content-Type не создаёт бронирование")
  void createBooking_withWrongContentType_returnsClientOrServerError() {
    Booking payload = TestDataFactory.randomBooking();
    String json =
        "{\"firstname\":\""
            + payload.firstname()
            + "\",\"lastname\":\""
            + payload.lastname()
            + "\",\"totalprice\":"
            + payload.totalprice()
            + ",\"depositpaid\":"
            + payload.depositpaid()
            + ",\"bookingdates\":{\"checkin\":\""
            + payload.bookingdates().checkin()
            + "\",\"checkout\":\""
            + payload.bookingdates().checkout()
            + "\"},\"additionalneeds\":\""
            + payload.additionalneeds()
            + "\"}";

    Response response = bookingApi.createBookingRaw(json, "application/xml");

    assertThat(response.statusCode()).isGreaterThanOrEqualTo(400);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Получение бронирования по id")
  @DisplayName("GET /booking/{id} возвращает 404 для несуществующего бронирования")
  void getBookingById_withNonExistentId_returnsNotFound() {
    Response response = bookingApi.getBooking(999_999_999);

    assertThat(response.statusCode()).isEqualTo(404);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Обновление бронирования")
  @DisplayName("PUT /booking/{id} без токена возвращает forbidden")
  void updateBooking_withoutToken_returnsForbidden() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);

    Booking updated =
        new Booking("Unauthorized", "Update", 222, false, payload.bookingdates(), "None");

    Response response = bookingApi.updateBookingWithoutToken(bookingId, updated);
    assertThat(response.statusCode()).isEqualTo(403);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Обновление бронирования")
  @DisplayName("PUT /booking/{id} с невалидным токеном возвращает forbidden")
  void updateBooking_withInvalidToken_returnsForbidden() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);

    Booking updated = new Booking("Invalid", "Token", 333, true, payload.bookingdates(), "None");

    Response response = bookingApi.updateBooking(bookingId, updated, "not-a-valid-token");
    assertThat(response.statusCode()).isEqualTo(403);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Частичное обновление бронирования")
  @DisplayName("PATCH /booking/{id} без токена возвращает forbidden")
  void partialUpdateBooking_withoutToken_returnsForbidden() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);

    Response response =
        bookingApi.partialUpdateBookingWithoutToken(bookingId, Map.of("firstname", "NoAuthPatch"));
    assertThat(response.statusCode()).isEqualTo(403);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Частичное обновление бронирования")
  @DisplayName("PATCH /booking/{id} с невалидным токеном возвращает forbidden")
  void partialUpdateBooking_withInvalidToken_returnsForbidden() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);

    Response response =
        bookingApi.partialUpdateBooking(
            bookingId, Map.of("firstname", "InvalidPatch"), "not-a-valid-token");
    assertThat(response.statusCode()).isEqualTo(403);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Удаление бронирования")
  @DisplayName("DELETE /booking/{id} без токена возвращает forbidden")
  void deleteBooking_withoutToken_returnsForbidden() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);

    Response response = bookingApi.deleteBookingWithoutToken(bookingId);
    assertThat(response.statusCode()).isEqualTo(403);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Документация Restful Booker API",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Удаление бронирования")
  @DisplayName("DELETE /booking/{id} с невалидным токеном возвращает forbidden")
  void deleteBooking_withInvalidToken_returnsForbidden() {
    Booking payload = TestDataFactory.randomBooking();
    int bookingId = createAndTrackBooking(payload);

    Response response = bookingApi.deleteBooking(bookingId, "not-a-valid-token");
    assertThat(response.statusCode()).isEqualTo(403);
  }

  private int createAndTrackBooking(Booking payload) {
    int bookingId = bookingApi.asBookingResponse(bookingApi.createBooking(payload)).bookingid();
    createdBookingIds.add(bookingId);
    return bookingId;
  }
}
