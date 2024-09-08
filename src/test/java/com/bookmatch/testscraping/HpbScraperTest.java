package com.bookmatch.testscraping;

import com.bookmatch.webscrapers.HpbScraper;
import com.bookmatch.ProductDao;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import com.bookmatch.webscrapers.FirefoxWebDriverProvider;

import static org.mockito.Mockito.*;

public class HpbScraperTest {

    /**
     * Test the run method of {@link HpbScraper}.
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

        // Creating the HpbScraper with the mocked dependencies
        HpbScraper hpbScraper = new HpbScraper(mockWebDriverProvider);
        hpbScraper.setProductDao(mockProductDao);

        // Running the scraper
        hpbScraper.start();

        // Verifying interactions
        verify(mockWebDriverProvider, atLeastOnce()).getWebDriver();
    }
}
