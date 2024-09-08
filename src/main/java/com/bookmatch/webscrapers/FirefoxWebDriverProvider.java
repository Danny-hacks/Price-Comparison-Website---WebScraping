package com.bookmatch.webscrapers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.stereotype.Component;

/**
 * The {@code FirefoxWebDriverProvider} class implements {@link WebDriverProvider}
 * to provide instances of a headless Firefox WebDriver.
 * It is annotated with {@link Component} to indicate that it is a Spring component
 * and can be automatically discovered and registered by the Spring container.
 */
@Component
public class FirefoxWebDriverProvider implements WebDriverProvider {

    /**
     * Provides a configured instance of a headless Firefox WebDriver.
     *
     * @return A configured instance of the Firefox WebDriver.
     */
    @Override
    public WebDriver getWebDriver() {
        // Set the system property for the GeckoDriver executable
        System.setProperty("webdriver.gecko.driver", "C:/Users/obief/geckodriver.exe");

        // Configure FirefoxOptions for headless mode
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);

        // Create a new instance of the Firefox driver with configured options
        return new FirefoxDriver(options);
    }
}
