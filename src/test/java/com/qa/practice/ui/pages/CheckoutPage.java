package com.qa.practice.ui.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class CheckoutPage {
  private final SelenideElement title = $(".title");
  private final SelenideElement firstNameInput = $("#first-name");
  private final SelenideElement lastNameInput = $("#last-name");
  private final SelenideElement postalCodeInput = $("#postal-code");
  private final SelenideElement continueButton = $("#continue");
  private final SelenideElement finishButton = $("#finish");
  private final SelenideElement completeHeader = $(".complete-header");
  private final SelenideElement errorMessage = $("h3[data-test='error']");

  @Step("Verify checkout step one opened")
  public CheckoutPage shouldBeStepOne() {
    title.shouldBe(visible).shouldHave(text("Checkout: Your Information"));
    return this;
  }

  @Step("Fill checkout info: {firstName} {lastName}")
  public CheckoutPage fillCustomerInfo(String firstName, String lastName, String postalCode) {
    firstNameInput.setValue(firstName);
    lastNameInput.setValue(lastName);
    postalCodeInput.setValue(postalCode);
    continueButton.shouldBe(visible).click();
    return this;
  }

  @Step("Submit checkout info: {firstName} {lastName} / {postalCode}")
  public CheckoutPage submitCustomerInfo(String firstName, String lastName, String postalCode) {
    firstNameInput.setValue(firstName);
    lastNameInput.setValue(lastName);
    postalCodeInput.setValue(postalCode);
    continueButton.shouldBe(visible).click();
    return this;
  }

  @Step("Verify checkout error message: {expectedMessage}")
  public CheckoutPage shouldShowError(String expectedMessage) {
    errorMessage.shouldBe(visible).shouldHave(text(expectedMessage));
    return this;
  }

  @Step("Complete checkout and verify success")
  public CheckoutPage finishAndVerifySuccess() {
    title.shouldBe(visible).shouldHave(text("Checkout: Overview"));
    finishButton.shouldBe(visible).click();
    completeHeader.shouldBe(visible).shouldHave(text("Thank you for your order!"));
    return this;
  }
}
