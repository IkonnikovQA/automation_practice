package com.qa.practice.data;

import com.qa.practice.api.models.Booking;
import com.qa.practice.data.builders.BookingBuilder;

public final class TestDataFactory {

  private TestDataFactory() {}

  public static Booking randomBooking() {
    return BookingBuilder.random().build();
  }
}
