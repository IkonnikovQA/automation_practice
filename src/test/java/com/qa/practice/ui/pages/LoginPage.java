package com.qa.practice.ui.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class LoginPage {
  private final SelenideElement usernameInput = $("#user-name");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement loginButton = $("#login-button");
  private final SelenideElement errorMessage = $("h3[data-test='error']");

  @Step("Open SauceDemo login page")
  public LoginPage openPage() {
    open("/");
    usernameInput.shouldBe(visible);
    return this;
  }

  @Step("Login as {username}")
  public InventoryPage loginAs(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    loginButton.click();
    return new InventoryPage();
  }

  @Step("Login should fail with message: {message}")
  public LoginPage shouldShowError(String message) {
    errorMessage.shouldBe(visible).shouldHave(text(message));
    return this;
  }
}
