package com.qa.practice.ui.pages;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class InventoryPage {
  private final SelenideElement title = $(".title");
  private final ElementsCollection inventoryItems = $$(".inventory_item");
  private final SelenideElement addBackpackButton = $("#add-to-cart-sauce-labs-backpack");
  private final SelenideElement cartBadge = $(".shopping_cart_badge");

  @Step("Verify inventory page opened")
  public InventoryPage shouldBeOpened() {
    title.shouldBe(visible).shouldHave(text("Products"));
    inventoryItems.shouldHave(sizeGreaterThan(0));
    return this;
  }

  @Step("Add backpack to cart")
  public InventoryPage addBackpackToCart() {
    addBackpackButton.shouldBe(visible).click();
    return this;
  }

  @Step("Verify cart badge count is {expectedCount}")
  public InventoryPage shouldHaveCartCount(String expectedCount) {
    cartBadge.shouldBe(visible).shouldHave(text(expectedCount));
    return this;
  }
}
