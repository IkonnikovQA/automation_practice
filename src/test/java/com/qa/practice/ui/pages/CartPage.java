package com.qa.practice.ui.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class CartPage {
  private final SelenideElement title = $("span[data-test='title']");
  private final SelenideElement cartItemName = $("div[data-test='inventory-item-name']");
  private final ElementsCollection cartItems = $$("div[data-test='inventory-item']");
  private final SelenideElement checkoutButton = $("#checkout");

  @Step("Проверить, что открыта корзина")
  public CartPage shouldBeOpened() {
    title.shouldBe(visible).shouldHave(text("Your Cart"));
    return this;
  }

  @Step("Проверить наличие товара {itemName} в корзине")
  public CartPage shouldContainItem(String itemName) {
    cartItemName.shouldBe(visible).shouldHave(text(itemName));
    return this;
  }

  @Step("Проверить, что в корзине {count} товаров")
  public CartPage shouldHaveItemsCount(int count) {
    cartItems.shouldHave(com.codeborne.selenide.CollectionCondition.size(count));
    return this;
  }

  @Step("Перейти к оформлению заказа")
  public CheckoutPage checkout() {
    checkoutButton.shouldBe(visible).click();
    return new CheckoutPage();
  }
}
