package com.qa.practice.data.builders;

import com.qa.practice.ui.models.CheckoutCustomer;

public class CheckoutCustomerBuilder {
  private String firstName = "Oleg";
  private String lastName = "QA";
  private String postalCode = "123456";

  private CheckoutCustomerBuilder() {}

  public static CheckoutCustomerBuilder valid() {
    return new CheckoutCustomerBuilder();
  }

  public CheckoutCustomerBuilder withFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public CheckoutCustomerBuilder withLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public CheckoutCustomerBuilder withPostalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  public CheckoutCustomer build() {
    return new CheckoutCustomer(firstName, lastName, postalCode);
  }
}
