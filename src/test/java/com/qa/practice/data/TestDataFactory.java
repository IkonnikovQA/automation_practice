package com.qa.practice.data;

import com.qa.practice.api.models.Booking;
import com.qa.practice.api.models.BookingDates;
import java.time.LocalDate;
import java.util.UUID;

public final class TestDataFactory {

  private TestDataFactory() {}

  public static Booking randomBooking() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    LocalDate checkin = LocalDate.now().plusDays(7);
    LocalDate checkout = checkin.plusDays(3);

    return new Booking(
        "Auto",
        "Tester-" + suffix,
        150,
        true,
        new BookingDates(checkin.toString(), checkout.toString()),
        "Breakfast");
  }
}
