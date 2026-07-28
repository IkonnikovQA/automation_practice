package com.qa.practice.tests.api;

import com.qa.practice.api.client.BookingApi;
import com.qa.practice.api.models.Booking;
import com.qa.practice.api.models.BookingDates;
import com.qa.practice.api.models.BookingResponse;
import com.qa.practice.data.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Bookings")
@Tag("api")
@Tag("smoke")
public class BookingApiTest {

    private final BookingApi bookingApi = new BookingApi();

    @Test
    @Story("List bookings")
    @DisplayName("GET /booking returns list of booking ids")
    void getBookingIds_returnsList() {
        Response response = bookingApi.getBookingIds();

        assertThat(response.statusCode()).isEqualTo(200);
        List<Map<String, Integer>> ids = response.jsonPath().getList("$");
        assertThat(ids).isNotEmpty();
        assertThat(ids.get(0)).containsKey("bookingid");
    }

    @Test
    @Story("Create booking")
    @DisplayName("POST /booking creates a booking and returns id")
    void createBooking_returnsIdAndBody() {
        Booking payload = TestDataFactory.randomBooking();

        Response response = bookingApi.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(200);
        BookingResponse created = bookingApi.asBookingResponse(response);
        assertThat(created.bookingid()).isPositive();
        assertThat(created.booking().firstname()).isEqualTo(payload.firstname());
        assertThat(created.booking().lastname()).isEqualTo(payload.lastname());
        assertThat(created.booking().totalprice()).isEqualTo(payload.totalprice());
        assertThat(created.booking().bookingdates().checkin()).isEqualTo(payload.bookingdates().checkin());
    }

    @Test
    @Story("Get booking by id")
    @DisplayName("GET /booking/{id} returns created booking")
    void getBookingById_returnsCreatedBooking() {
        Booking payload = TestDataFactory.randomBooking();
        int bookingId = bookingApi.asBookingResponse(bookingApi.createBooking(payload)).bookingid();

        Response response = bookingApi.getBooking(bookingId);

        assertThat(response.statusCode()).isEqualTo(200);
        Booking booking = bookingApi.asBooking(response);
        assertThat(booking.firstname()).isEqualTo(payload.firstname());
        assertThat(booking.lastname()).isEqualTo(payload.lastname());
        assertThat(booking.additionalneeds()).isEqualTo(payload.additionalneeds());
    }

    @Test
    @Story("Update booking")
    @DisplayName("PUT /booking/{id} updates booking with token")
    void updateBooking_withToken_updatesFields() {
        Booking payload = TestDataFactory.randomBooking();
        int bookingId = bookingApi.asBookingResponse(bookingApi.createBooking(payload)).bookingid();
        String token = bookingApi.createToken();

        Booking updated = new Booking(
                "Updated",
                "Name",
                999,
                false,
                new BookingDates(payload.bookingdates().checkin(), payload.bookingdates().checkout()),
                "Late checkout"
        );

        Response response = bookingApi.updateBooking(bookingId, updated, token);

        assertThat(response.statusCode()).isEqualTo(200);
        Booking body = bookingApi.asBooking(response);
        assertThat(body.firstname()).isEqualTo("Updated");
        assertThat(body.lastname()).isEqualTo("Name");
        assertThat(body.totalprice()).isEqualTo(999);
        assertThat(body.additionalneeds()).isEqualTo("Late checkout");
    }

    @Test
    @Story("Partial update booking")
    @DisplayName("PATCH /booking/{id} updates firstname only")
    void partialUpdateBooking_updatesFirstname() {
        Booking payload = TestDataFactory.randomBooking();
        int bookingId = bookingApi.asBookingResponse(bookingApi.createBooking(payload)).bookingid();
        String token = bookingApi.createToken();

        Response response = bookingApi.partialUpdateBooking(
                bookingId,
                Map.of("firstname", "Patched"),
                token
        );

        assertThat(response.statusCode()).isEqualTo(200);
        Booking body = bookingApi.asBooking(response);
        assertThat(body.firstname()).isEqualTo("Patched");
        assertThat(body.lastname()).isEqualTo(payload.lastname());
    }

    @Test
    @Story("Delete booking")
    @DisplayName("DELETE /booking/{id} removes booking")
    void deleteBooking_removesBooking() {
        Booking payload = TestDataFactory.randomBooking();
        int bookingId = bookingApi.asBookingResponse(bookingApi.createBooking(payload)).bookingid();
        String token = bookingApi.createToken();

        Response deleteResponse = bookingApi.deleteBooking(bookingId, token);
        assertThat(deleteResponse.statusCode()).isEqualTo(201);

        Response getResponse = bookingApi.getBooking(bookingId);
        assertThat(getResponse.statusCode()).isEqualTo(404);
    }
}
