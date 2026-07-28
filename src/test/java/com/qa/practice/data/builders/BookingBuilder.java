package com.qa.practice.data.builders;

import com.qa.practice.api.models.Booking;
import com.qa.practice.api.models.BookingDates;
import java.time.LocalDate;
import java.util.UUID;

public class BookingBuilder {
  private String firstname;
  private String lastname;
  private int totalprice;
  private boolean depositpaid;
  private BookingDates bookingdates;
  private String additionalneeds;

  private BookingBuilder() {}

  public static BookingBuilder random() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    LocalDate checkin = LocalDate.now().plusDays(7);
    LocalDate checkout = checkin.plusDays(3);

    BookingBuilder builder = new BookingBuilder();
    builder.firstname = "Auto";
    builder.lastname = "Tester-" + suffix;
    builder.totalprice = 150;
    builder.depositpaid = true;
    builder.bookingdates = new BookingDates(checkin.toString(), checkout.toString());
    builder.additionalneeds = "Breakfast";
    return builder;
  }

  public BookingBuilder withFirstname(String firstname) {
    this.firstname = firstname;
    return this;
  }

  public BookingBuilder withLastname(String lastname) {
    this.lastname = lastname;
    return this;
  }

  public BookingBuilder withTotalprice(int totalprice) {
    this.totalprice = totalprice;
    return this;
  }

  public BookingBuilder withDepositpaid(boolean depositpaid) {
    this.depositpaid = depositpaid;
    return this;
  }

  public BookingBuilder withBookingDates(BookingDates bookingdates) {
    this.bookingdates = bookingdates;
    return this;
  }

  public BookingBuilder withAdditionalneeds(String additionalneeds) {
    this.additionalneeds = additionalneeds;
    return this;
  }

  public Booking build() {
    return new Booking(firstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
  }
}
