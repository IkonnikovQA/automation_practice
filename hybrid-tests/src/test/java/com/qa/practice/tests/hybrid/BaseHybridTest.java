package com.qa.practice.tests.hybrid;

import com.codeborne.selenide.Selenide;
import com.qa.practice.ui.driver.SelenideSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SelenideSetup.class)
public abstract class BaseHybridTest {
  @AfterEach
  void tearDownBrowser() {
    Selenide.closeWebDriver();
  }
}
