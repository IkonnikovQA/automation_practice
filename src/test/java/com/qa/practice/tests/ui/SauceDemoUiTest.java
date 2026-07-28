package com.qa.practice.tests.ui;

import com.qa.practice.config.Config;
import com.qa.practice.ui.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("UI")
@Feature("SauceDemo")
@Tag("ui")
@Tag("regression")
public class SauceDemoUiTest extends BaseUiTest {
  private final LoginPage loginPage = new LoginPage();

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.BLOCKER)
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Login")
  @DisplayName("Valid user can login and see inventory")
  void validUserCanLogin() {
    loginPage.openPage().loginAs(Config.uiUsername(), Config.uiPassword()).shouldBeOpened();
  }

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.CRITICAL)
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Cart")
  @DisplayName("User can add backpack to cart")
  void userCanAddProductToCart() {
    loginPage
        .openPage()
        .loginAs(Config.uiUsername(), Config.uiPassword())
        .shouldBeOpened()
        .addBackpackToCart()
        .shouldHaveCartCount("1");
  }

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Login")
  @DisplayName("Locked out user sees login error")
  void lockedOutUserSeesError() {
    loginPage.openPage().loginAs("locked_out_user", Config.uiPassword());

    loginPage.shouldShowError("Sorry, this user has been locked out.");
  }
}
