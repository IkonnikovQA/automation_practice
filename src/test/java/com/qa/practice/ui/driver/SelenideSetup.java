package com.qa.practice.ui.driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.qa.practice.config.Config;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SelenideSetup implements BeforeAllCallback {
  private static boolean initialized;

  @Override
  public void beforeAll(ExtensionContext context) {
    if (initialized) {
      return;
    }

    Configuration.baseUrl = Config.uiBaseUrl();
    Configuration.browser = Config.uiBrowser();
    Configuration.headless = Config.uiHeadless();
    if (Config.uiRemoteUrl() != null) {
      Configuration.remote = Config.uiRemoteUrl();
    }
    Configuration.timeout = Config.uiTimeoutMs();
    Configuration.pageLoadTimeout = 20_000;
    Configuration.browserSize = "1920x1080";
    Configuration.savePageSource = false;
    Configuration.screenshots = true;

    SelenideLogger.addListener(
        "AllureSelenide", new AllureSelenide().savePageSource(false).screenshots(true));
    initialized = true;
  }
}
