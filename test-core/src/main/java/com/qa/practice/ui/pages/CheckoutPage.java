package com.qa.practice.ui.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import com.qa.practice.ui.models.CheckoutCustomer;
import io.qameta.allure.Step;

public class CheckoutPage {
  private final SelenideElement title = $("span[data-test='title']");
  private final SelenideElement firstNameInput = $("#first-name");
  private final SelenideElement lastNameInput = $("#last-name");
  private final SelenideElement postalCodeInput = $("#postal-code");
  private final SelenideElement continueButton = $("#continue");
  private final SelenideElement finishButton = $("#finish");
  private final SelenideElement completeHeader = $("h2[data-test='complete-header']");
  private final SelenideElement errorMessage = $("h3[data-test='error']");

  @Step("Проверить, что открыт шаг 1 checkout")
  public CheckoutPage shouldBeStepOne() {
    title.shouldBe(visible).shouldHave(text("Checkout: Your Information"));
    return this;
  }

  @Step("Заполнить данные покупателя: {firstName} {lastName}")
  public CheckoutPage fillCustomerInfo(String firstName, String lastName, String postalCode) {
    firstNameInput.setValue(firstName);
    lastNameInput.setValue(lastName);
    postalCodeInput.setValue(postalCode);
    continueButton.shouldBe(visible).click();
    return this;
  }

  @Step("Заполнить данные покупателя из модели")
  public CheckoutPage fillCustomerInfo(CheckoutCustomer customer) {
    return fillCustomerInfo(customer.firstName(), customer.lastName(), customer.postalCode());
  }

  @Step("Отправить данные покупателя: {firstName} {lastName} / {postalCode}")
  public CheckoutPage submitCustomerInfo(String firstName, String lastName, String postalCode) {
    firstNameInput.setValue(firstName);
    lastNameInput.setValue(lastName);
    postalCodeInput.setValue(postalCode);
    continueButton.shouldBe(visible).click();
    return this;
  }

  @Step("Проверить ошибку checkout: {expectedMessage}")
  public CheckoutPage shouldShowError(String expectedMessage) {
    errorMessage.shouldBe(visible).shouldHave(text(expectedMessage));
    return this;
  }

  @Step("Завершить checkout и проверить успех")
  public CheckoutPage finishAndVerifySuccess() {
    title.shouldBe(visible).shouldHave(text("Checkout: Overview"));
    finishButton.shouldBe(visible).click();
    completeHeader.shouldBe(visible).shouldHave(text("Thank you for your order!"));
    return this;
  }
}
