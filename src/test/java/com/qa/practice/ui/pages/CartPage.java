package com.qa.practice.ui.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class CartPage {
  private final SelenideElement title = $(".title");
  private final SelenideElement cartItemName = $(".inventory_item_name");
  private final ElementsCollection cartItems = $$(".cart_item");
  private final SelenideElement checkoutButton = $("#checkout");

  @Step("Verify cart page opened")
  public CartPage shouldBeOpened() {
    title.shouldBe(visible).shouldHave(text("Your Cart"));
    return this;
  }

  @Step("Verify item {itemName} exists in cart")
  public CartPage shouldContainItem(String itemName) {
    cartItemName.shouldBe(visible).shouldHave(text(itemName));
    return this;
  }

  @Step("Verify cart contains {count} items")
  public CartPage shouldHaveItemsCount(int count) {
    cartItems.shouldHave(com.codeborne.selenide.CollectionCondition.size(count));
    return this;
  }

  @Step("Proceed to checkout")
  public CheckoutPage checkout() {
    checkoutButton.shouldBe(visible).click();
    return new CheckoutPage();
  }
}
