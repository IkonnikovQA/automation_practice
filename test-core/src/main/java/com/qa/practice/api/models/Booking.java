package com.qa.practice.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Booking(
    String firstname,
    String lastname,
    int totalprice,
    boolean depositpaid,
    BookingDates bookingdates,
    String additionalneeds) {}
