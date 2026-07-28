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
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Cart")
  @DisplayName("User can remove product from cart")
  void userCanRemoveProductFromCart() {
    loginPage
        .openPage()
        .loginAs(Config.uiUsername(), Config.uiPassword())
        .shouldBeOpened()
        .addBackpackToCart()
        .shouldHaveCartCount("1")
        .removeBackpackFromCart()
        .shouldNotHaveCartBadge();
  }

  @Test
  @Tag("regression")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Catalog")
  @DisplayName("User can sort products by price low to high")
  void userCanSortProductsByPriceLowToHigh() {
    loginPage
        .openPage()
        .loginAs(Config.uiUsername(), Config.uiPassword())
        .shouldBeOpened()
        .sortByPriceLowToHigh()
        .shouldHaveFirstItemName("Sauce Labs Onesie");
  }

  @Test
  @Tag("smoke")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.BLOCKER)
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Checkout")
  @DisplayName("User can complete checkout flow")
  void userCanCompleteCheckoutFlow() {
    loginPage
        .openPage()
        .loginAs(Config.uiUsername(), Config.uiPassword())
        .shouldBeOpened()
        .addBackpackToCart()
        .openCart()
        .shouldBeOpened()
        .shouldContainItem("Sauce Labs Backpack")
        .checkout()
        .shouldBeStepOne()
        .fillCustomerInfo("Oleg", "QA", "123456")
        .finishAndVerifySuccess();
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

  @Test
  @Tag("negative")
  @Owner("IkonnikovQA")
  @Severity(SeverityLevel.NORMAL)
  @Link(name = "SauceDemo", url = "https://www.saucedemo.com/")
  @Story("Login")
  @DisplayName("User sees error for invalid password")
  void invalidPasswordShowsError() {
    loginPage.openPage().loginAs(Config.uiUsername(), "wrong_password");
    loginPage.shouldShowError("Username and password do not match any user in this service");
  }
}
