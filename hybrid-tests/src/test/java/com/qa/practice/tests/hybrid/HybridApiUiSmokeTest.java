package com.qa.practice.tests.hybrid;

import static org.assertj.core.api.Assertions.assertThat;

import com.qa.practice.api.client.BookingApi;
import com.qa.practice.api.models.Booking;
import com.qa.practice.api.models.BookingResponse;
import com.qa.practice.config.Config;
import com.qa.practice.data.builders.BookingBuilder;
import com.qa.practice.ui.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("Hybrid")
@Feature("API + UI оркестрация")
@Tag("hybrid")
@Tag("regression")
public class HybridApiUiSmokeTest extends BaseHybridTest {

  private final BookingApi bookingApi = new BookingApi();
  private final LoginPage loginPage = new LoginPage();
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
  @Link(name = "Restful Booker API", url = "https://restful-booker.herokuapp.com/apidoc/index.html")
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Критический путь через слои")
  @DisplayName("API health + create booking, затем UI login в каталог")
  void apiReadyThenUiCriticalPath() {
    assertApiHealthy();
    int bookingId = createAndVerifyBookingViaApi();
    assertThat(bookingId).isPositive();
    openStorefrontViaUi();
  }

  @Step("Проверить health Restful Booker (GET /ping)")
  private void assertApiHealthy() {
    Response ping = bookingApi.ping();
    assertThat(ping.statusCode()).isEqualTo(201);
  }

  @Step("Создать бронирование через API и прочитать его по id")
  private int createAndVerifyBookingViaApi() {
    Booking payload = BookingBuilder.random().build();
    BookingResponse created =
        bookingApi
            .createBooking(payload)
            .then()
            .statusCode(200)
            .extract()
            .as(BookingResponse.class);

    createdBookingIds.add(created.bookingid());
    token = bookingApi.createToken();

    Booking fetched =
        bookingApi
            .getBooking(created.bookingid())
            .then()
            .statusCode(200)
            .extract()
            .as(Booking.class);

    assertThat(fetched.firstname()).isEqualTo(payload.firstname());
    assertThat(fetched.lastname()).isEqualTo(payload.lastname());
    return created.bookingid();
  }

  @Step("Открыть SauceDemo и войти валидным пользователем")
  private void openStorefrontViaUi() {
    loginPage.openPage().loginAs(Config.uiUsername(), Config.uiPassword()).shouldBeOpened();
  }
}
