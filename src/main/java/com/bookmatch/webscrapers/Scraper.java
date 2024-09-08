package com.bookmatch.webscrapers;

import org.springframework.stereotype.Service;

/**
 * The {@code Scraper} class manages the simultaneous operation of several web scraping threads.
 * It gathers individual scrapers and launches them simultaneously.
 */
@Service
public class Scraper {

    /** The scraper for the Waterstones website. */
    WaterStonesScraper waterStonesScraper;

    /** The scraper for the Wordery website. */
    WorderyScraper worderyScraper;

    /** The scraper for the Half Price Books website. */
    HpbScraper hpbScraper;

    /** The scraper for the QBD Books website. */
    QbdScraper qbdScraper;

    /** The scraper for the Dubray Books website. */
    DubrayScraper dubrayScraper;

    /** The scraper for the Owl's Nest Books website. */
    OwlsNestScraper owlsNestScraper;

    /**
     * Sets the WaterStonesScraper instance for this Scraper.
     *
     * @param waterStonesScraper The WaterStonesScraper instance to be set.
     */
    public void setWaterStonesScraper(WaterStonesScraper waterStonesScraper) {
        this.waterStonesScraper = waterStonesScraper;
    }

    /**
     * Sets the WorderyScraper instance for this Scraper.
     *
     * @param worderyScraper The WorderyScraper instance to be set.
     */
    public void setWorderyScraper(WorderyScraper worderyScraper) {
        this.worderyScraper = worderyScraper;
    }

    /**
     * Sets the HpbScraper instance for this Scraper.
     *
     * @param hpbScraper The HpbScraper instance to be set.
     */
    public void setHpbScraper(HpbScraper hpbScraper) {
        this.hpbScraper = hpbScraper;
    }

    /**
     * Sets the QbdScraper instance for this Scraper.
     *
     * @param qbdScraper The QbdScraper instance to be set.
     */
    public void setQbdScraper(QbdScraper qbdScraper) {
        this.qbdScraper = qbdScraper;
    }

    /**
     * Sets the DubrayScraper instance for this Scraper.
     *
     * @param dubrayScraper The DubrayScraper instance to be set.
     */
    public void setDubrayScraper(DubrayScraper dubrayScraper) {
        this.dubrayScraper = dubrayScraper;
    }

    /**
     * Sets the OwlsNestScraper instance for this Scraper.
     *
     * @param owlsNestScraper The OwlsNestScraper instance to be set.
     */
    public void setOwlsNestScraper(OwlsNestScraper owlsNestScraper) {
        this.owlsNestScraper = owlsNestScraper;
    }

    /**
     * Initiates the execution of individual scrapers concurrently.
     * Each scraper runs in a separate thread.
     */
    public void scrape() {
        dubrayScraper.start();
        qbdScraper.start();
        hpbScraper.start();
        waterStonesScraper.start();
        worderyScraper.start();
        owlsNestScraper.start();
    }
}
