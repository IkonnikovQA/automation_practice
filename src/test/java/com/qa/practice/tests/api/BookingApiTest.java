package com.qa.practice.tests.api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

import com.qa.practice.api.client.BookingApi;
import com.qa.practice.api.models.Booking;
import com.qa.practice.api.models.BookingDates;
import com.qa.practice.api.models.BookingResponse;
import com.qa.practice.data.builders.BookingBuilder;
import com.qa.practice.data.TestDataFactory;
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
@Feature("Bookings")
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
            "Cleanup failed for bookingId=" + bookingId + ", status=" + response.statusCode());
      }
    }
    createdBookingIds.clear();
  }

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("List bookings")
  @DisplayName("GET /booking returns list of booking ids")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Create booking")
  @DisplayName("POST /booking creates a booking and returns id")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Create booking")
  @DisplayName("POST /booking supports null additional needs")
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
  @Tag("smoke")
  @Tag("contract")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Get booking by id")
  @DisplayName("GET /booking/{id} returns created booking")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("List bookings")
  @DisplayName("GET /booking supports firstname and lastname filters")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("List bookings")
  @DisplayName("GET /booking supports firstname-only filter")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("List bookings")
  @DisplayName("GET /booking supports lastname-only filter")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("List bookings")
  @DisplayName("GET /booking with unknown filters returns empty list")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Update booking")
  @DisplayName("PUT /booking/{id} updates booking with token")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Partial update booking")
  @DisplayName("PATCH /booking/{id} updates firstname only")
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
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Delete booking")
  @DisplayName("DELETE /booking/{id} removes booking")
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
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Get booking by id")
  @DisplayName("GET /booking/{id} returns 404 for non-existent booking")
  void getBookingById_withNonExistentId_returnsNotFound() {
    Response response = bookingApi.getBooking(999_999_999);

    assertThat(response.statusCode()).isEqualTo(404);
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Update booking")
  @DisplayName("PUT /booking/{id} without token returns forbidden")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Update booking")
  @DisplayName("PUT /booking/{id} with invalid token returns forbidden")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Delete booking")
  @DisplayName("DELETE /booking/{id} without token returns forbidden")
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
      name = "Restful Booker API Docs",
      url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Story("Delete booking")
  @DisplayName("DELETE /booking/{id} with invalid token returns forbidden")
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
