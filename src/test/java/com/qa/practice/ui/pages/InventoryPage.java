package com.qa.practice.ui.pages;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.disappear;
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
  private final SelenideElement removeBackpackButton = $("#remove-sauce-labs-backpack");
  private final SelenideElement cartBadge = $(".shopping_cart_badge");
  private final SelenideElement cartLink = $(".shopping_cart_link");
  private final SelenideElement sortDropdown = $(".product_sort_container");
  private final ElementsCollection itemNames = $$(".inventory_item_name");

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

  @Step("Remove backpack from cart")
  public InventoryPage removeBackpackFromCart() {
    removeBackpackButton.shouldBe(visible).click();
    return this;
  }

  @Step("Verify cart badge is not visible")
  public InventoryPage shouldNotHaveCartBadge() {
    cartBadge.should(disappear);
    return this;
  }

  @Step("Sort products by price low to high")
  public InventoryPage sortByPriceLowToHigh() {
    sortDropdown.selectOptionByValue("lohi");
    return this;
  }

  @Step("Verify first product name is {expectedName}")
  public InventoryPage shouldHaveFirstItemName(String expectedName) {
    itemNames.first().shouldBe(visible).shouldHave(text(expectedName));
    return this;
  }

  @Step("Open cart page")
  public CartPage openCart() {
    cartLink.shouldBe(visible).click();
    return new CartPage();
  }
}
