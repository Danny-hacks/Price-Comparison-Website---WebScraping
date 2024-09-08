package com.bookmatch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.bookmatch.webscrapers.*;

/**
 * AppConfig class is a configuration class responsible for defining Spring beans for the application.
 */
@Configuration
@ComponentScan(basePackages = "com.bookmatch.webscrapers")
public class AppConfig {

    private final FirefoxWebDriverProvider webDriverProvider;

    /**
     * Constructs an {@code AppConfig} instance with the specified {@code FirefoxWebDriverProvider}.
     *
     * @param webDriverProvider The provider for obtaining WebDriver instances.
     */
    public AppConfig(FirefoxWebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    /**
     * Creates and initializes a ProductDao bean.
     *
     * @return The initialized ProductDao bean.
     */
    @Bean
    public ProductDao getProductDao() {
        ProductDao tmpProductDao = new ProductDao();
        tmpProductDao.init();
        return tmpProductDao;
    }

    /**
     * Creates a WaterStonesScraper bean and sets its ProductDao dependency.
     *
     * @return The configured WaterStonesScraper bean.
     */
    @Bean
    public WaterStonesScraper waterStonesScraper() {
        WaterStonesScraper tmpWaterStonesScraper = new WaterStonesScraper(webDriverProvider);
        tmpWaterStonesScraper.setProductDao(getProductDao());
        return tmpWaterStonesScraper;
    }

    /**
     * Creates a WorderyScraper bean and sets its ProductDao dependency.
     *
     * @return The configured WorderyScraper bean.
     */
    @Bean
    public WorderyScraper worderyScraper() {
        WorderyScraper tmpWorderyScraper = new WorderyScraper(webDriverProvider);
        tmpWorderyScraper.setProductDao(getProductDao());
        return tmpWorderyScraper;
    }

    /**
     * Creates an HpbScraper bean and sets its ProductDao dependency.
     *
     * @return The configured HpbScraper bean.
     */
    @Bean
    public HpbScraper hpbScraper() {
        HpbScraper tmpHpbScraper = new HpbScraper(webDriverProvider);
        tmpHpbScraper.setProductDao(getProductDao());
        return tmpHpbScraper;
    }

    /**
     * Creates a QbdScraper bean and sets its ProductDao dependency.
     *
     * @return The configured QbdScraper bean.
     */
    @Bean
    public QbdScraper qbdScraper() {
        QbdScraper tmpQbdScraper = new QbdScraper(webDriverProvider);
        tmpQbdScraper.setProductDao(getProductDao());
        return tmpQbdScraper;
    }

    /**
     * Creates a DubrayScraper bean and sets its ProductDao dependency.
     *
     * @return The configured DubrayScraper bean.
     */
    @Bean
    public DubrayScraper dubrayScraper() {
        DubrayScraper tmpDubrayScraper = new DubrayScraper(webDriverProvider);
        tmpDubrayScraper.setProductDao(getProductDao());
        return tmpDubrayScraper;
    }

    /**
     * Creates an OwlsNestScraper bean and sets its ProductDao dependency.
     *
     * @return The configured OwlsNestScraper bean.
     */
    @Bean
    public OwlsNestScraper owlsNestScraper() {
        OwlsNestScraper tmpOwlsNestScraper = new OwlsNestScraper(webDriverProvider);
        tmpOwlsNestScraper.setProductDao(getProductDao());
        return tmpOwlsNestScraper;
    }

    /**
     * Creates a Scraper bean and sets its individual scraper dependencies.
     *
     * @return The configured Scraper bean.
     */
    @Bean
    public Scraper scraper() {
        Scraper tmpScraper = new Scraper();
        tmpScraper.setDubrayScraper(dubrayScraper());
        tmpScraper.setHpbScraper(hpbScraper());
        tmpScraper.setQbdScraper(qbdScraper());
        tmpScraper.setWaterStonesScraper(waterStonesScraper());
        tmpScraper.setWorderyScraper(worderyScraper());
        tmpScraper.setOwlsNestScraper(owlsNestScraper());
        return tmpScraper;
    }
}
