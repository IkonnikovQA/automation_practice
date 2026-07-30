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
  private final SelenideElement title = $("span[data-test='title']");
  private final ElementsCollection inventoryItems = $$(".inventory_item");
  private final SelenideElement addBackpackButton = $("#add-to-cart-sauce-labs-backpack");
  private final SelenideElement addBikeLightButton = $("#add-to-cart-sauce-labs-bike-light");
  private final SelenideElement removeBackpackButton = $("#remove-sauce-labs-backpack");
  private final SelenideElement cartBadge = $("span[data-test='shopping-cart-badge']");
  private final SelenideElement cartLink = $("a[data-test='shopping-cart-link']");
  private final SelenideElement burgerMenuButton = $("#react-burger-menu-btn");
  private final SelenideElement logoutLink = $("#logout_sidebar_link");
  private final SelenideElement sortDropdown = $("select[data-test='product-sort-container']");
  private final ElementsCollection itemNames = $$("div[data-test='inventory-item-name']");

  @Step("Проверить, что открыт каталог")
  public InventoryPage shouldBeOpened() {
    title.shouldBe(visible).shouldHave(text("Products"));
    inventoryItems.shouldHave(sizeGreaterThan(0));
    return this;
  }

  @Step("Добавить рюкзак в корзину")
  public InventoryPage addBackpackToCart() {
    addBackpackButton.shouldBe(visible).click();
    return this;
  }

  @Step("Добавить фонарь в корзину")
  public InventoryPage addBikeLightToCart() {
    addBikeLightButton.shouldBe(visible).click();
    return this;
  }

  @Step("Проверить badge корзины: {expectedCount}")
  public InventoryPage shouldHaveCartCount(String expectedCount) {
    cartBadge.shouldBe(visible).shouldHave(text(expectedCount));
    return this;
  }

  @Step("Удалить рюкзак из корзины")
  public InventoryPage removeBackpackFromCart() {
    removeBackpackButton.shouldBe(visible).click();
    return this;
  }

  @Step("Проверить, что badge корзины скрыт")
  public InventoryPage shouldNotHaveCartBadge() {
    cartBadge.should(disappear);
    return this;
  }

  @Step("Отсортировать товары по цене low→high")
  public InventoryPage sortByPriceLowToHigh() {
    sortDropdown.selectOptionByValue("lohi");
    return this;
  }

  @Step("Проверить название первого товара: {expectedName}")
  public InventoryPage shouldHaveFirstItemName(String expectedName) {
    itemNames.first().shouldBe(visible).shouldHave(text(expectedName));
    return this;
  }

  @Step("Открыть корзину")
  public CartPage openCart() {
    cartLink.shouldBe(visible).click();
    return new CartPage();
  }

  @Step("Выйти из аккаунта")
  public LoginPage logout() {
    burgerMenuButton.shouldBe(visible).click();
    logoutLink.shouldBe(visible).click();
    return new LoginPage();
  }
}
