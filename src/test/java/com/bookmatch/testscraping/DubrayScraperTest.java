package com.bookmatch.testscraping;

import com.bookmatch.webscrapers.DubrayScraper;
import com.bookmatch.ProductDao;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import com.bookmatch.webscrapers.FirefoxWebDriverProvider;

import static org.mockito.Mockito.*;

public class DubrayScraperTest {

    /**
     * Test the run method of {@link DubrayScraper}.
     * It mocks the dependencies, creates the scraper with mocked dependencies, runs the scraper,
     * and verifies interactions with the mocked dependencies.
     */
    @Test
    public void testRun() {
        // Mocking the dependencies
        FirefoxWebDriverProvider mockWebDriverProvider = mock(FirefoxWebDriverProvider.class);
        ProductDao mockProductDao = mock(ProductDao.class);
        WebDriver mockDriver = mock(WebDriver.class);
        when(mockWebDriverProvider.getWebDriver()).thenReturn(mockDriver);

        // Creating the DubrayScraper with the mocked dependencies
        DubrayScraper dubrayScraper = new DubrayScraper(mockWebDriverProvider);
        dubrayScraper.setProductDao(mockProductDao);

        // Running the scraper
        dubrayScraper.start();

        // Verifying interactions
        verify(mockWebDriverProvider, atLeastOnce()).getWebDriver();
    }
}
