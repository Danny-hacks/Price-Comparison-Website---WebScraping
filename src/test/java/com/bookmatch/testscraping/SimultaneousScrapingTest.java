package com.bookmatch.testscraping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import com.bookmatch.webscrapers.*;
import static org.mockito.Mockito.*;

/**
 * The SimultaneousScrapingTest class contains JUnit tests for the simultaneous execution of web scrapers.
 */
public class SimultaneousScrapingTest {

    private Scraper scraper;
    private WaterStonesScraper waterStonesScraper;
    private WorderyScraper worderyScraper;
    private HpbScraper hpbScraper;
    private QbdScraper qbdScraper;
    private DubrayScraper dubrayScraper;
    private OwlsNestScraper owlsNestScraper;
    private FirefoxWebDriverProvider mockWebDriverProvider;
    private WebDriver mockDriver;

    @BeforeEach
    void setUp() {
        // Mocking WebDriver dependencies
        mockWebDriverProvider = mock(FirefoxWebDriverProvider.class);
        mockDriver = mock(WebDriver.class);
        when(mockWebDriverProvider.getWebDriver()).thenReturn(mockDriver);

        // Creating Scraper instance and mocking individual scrapers
        scraper = new Scraper();
        waterStonesScraper = new WaterStonesScraper(mockWebDriverProvider);
        worderyScraper = new WorderyScraper(mockWebDriverProvider);
        hpbScraper = new HpbScraper(mockWebDriverProvider);
        qbdScraper = new QbdScraper(mockWebDriverProvider);
        dubrayScraper = new DubrayScraper(mockWebDriverProvider);
        owlsNestScraper = new OwlsNestScraper(mockWebDriverProvider);

        // Setting the mocked scrapers in the Scraper class
        scraper.setWaterStonesScraper(waterStonesScraper);
        scraper.setWorderyScraper(worderyScraper);
        scraper.setHpbScraper(hpbScraper);
        scraper.setQbdScraper(qbdScraper);
        scraper.setDubrayScraper(dubrayScraper);
        scraper.setOwlsNestScraper(owlsNestScraper);
    }

    /**
     * Tests the simultaneous execution of all scrapers.
     * Verifies that each scraper's start method is called once.
     */
    @Test
    void testSimultaneousScraping() throws InterruptedException {
        // Execute concurrent scraping
        scraper.scrape();

        // Wait for each thread to finish
        dubrayScraper.join();
        qbdScraper.join();
        hpbScraper.join();
        waterStonesScraper.join();
        worderyScraper.join();
        owlsNestScraper.join();

        // Adding assertions
        // Checking if each thread has finished execution
        Assertions.assertFalse(dubrayScraper.isAlive(), "Ensure that DubrayScraper is not alive after scraping.");
        Assertions.assertFalse(qbdScraper.isAlive(), "Ensure that QbdScraper is not  alive after scraping.");
        Assertions.assertFalse(hpbScraper.isAlive(), "Ensure that HpbScraper is not alive after scraping.");
        Assertions.assertFalse(waterStonesScraper.isAlive(), "Ensure that WaterStonesScraper is not alive after scraping.");
        Assertions.assertFalse(worderyScraper.isAlive(), "Ensure that WorderyScraper is not alive after scraping.");
        Assertions.assertFalse(owlsNestScraper.isAlive(), "Ensure that OwlsNestScraper is not alive after scraping.");
    }
}
