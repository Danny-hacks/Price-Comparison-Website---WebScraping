package com.bookmatch.webscrapers;

import com.bookmatch.models.ComparePrices;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.bookmatch.models.Books;
import com.bookmatch.models.Price;
import com.bookmatch.ProductDao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * DubrayScraper is a web scraper for extracting book information from the Dubray Books website.
 * It extends the Thread class to allow concurrent scraping of data.
 */
public class DubrayScraper extends Thread {

    private final FirefoxWebDriverProvider webDriverProvider; // Provider for Firefox WebDriver instances
    public ProductDao productDao; // Data Access Object for handling database operations
    private static final int MAX_ITEMS = 500; // Maximum number of items to scrape
    private int itemCount = 0; // Counter for the number of items added

    /**
     * Constructor for DubrayScraper.
     *
     * @param webDriverProvider Provider for WebDriver instances (Firefox)
     */
    public DubrayScraper(FirefoxWebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    /**
     * Sets the ProductDao instance to be used for database operations.
     *
     * @param productDao ProductDao instance
     */
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Overrides the run method of Thread class to start scraping.
     * Initiates scraping process from the Dubray Books website.
     */
    @Override
    public void run() {
        int page = 1;
        do {
            if (itemCount >= MAX_ITEMS) {
                System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                break;
            }

            WebDriver driver = webDriverProvider.getWebDriver();
            driver.get("https://www.dubraybooks.ie/category/historical-romance?page=" + page + "&book_types=Paperback&sortBy=products");

            try {
                Thread.sleep(3000); // Wait for page to load
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Entering Dubray Website page: " + page);
            List<WebElement> bookList = driver.findElements(By.className("product-card"));

            if (bookList.isEmpty()) {
                break;
            }

            List<String> bookUrls = new ArrayList<>();
            for (WebElement book : bookList) {
                bookUrls.add(book.findElement(By.className("product-image")).findElement(By.tagName("a")).getAttribute("href"));
            }

            for (String bookUrl : bookUrls) {
                if (itemCount >= MAX_ITEMS) {
                    System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                    break;
                }

                WebDriver pageDriver = webDriverProvider.getWebDriver();
                pageDriver.get(bookUrl);

                try {
                    Thread.sleep(4000); // Wait for page to load
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    // Extract book details
                    WebElement titleElement = pageDriver.findElement(By.xpath("//*[@id=\"product-title\"]"));
                    String title = titleElement.getText();
                    String author = pageDriver.findElement(By.xpath("//*[@id=\"product-authorname\"]")).getText();
                    WebElement descriptionElement = pageDriver.findElement(By.xpath("//*[@id=\"product-description\"]"));
                    String description = descriptionElement.getText().split("\\r?\\n|\\.")[0].trim(); // Take the first part of description
                    String publicationDateStr = pageDriver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[3]/div/div/div[3]/div[1]/p[4]/span")).getText();
                    String imageUrl = pageDriver.findElement(By.xpath("//*[@id=\"product-image\"]")).getAttribute("src");
                    String priceStr = pageDriver.findElement(By.xpath("//*[@id=\"product-current-price\"]")).getText().replace("€", "").trim();
                    double price = Double.parseDouble(priceStr);

                    // Parse publication date
                    LocalDate publicationDate = parseDate(publicationDateStr);

                    // Display extracted information
                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("Description: " + description);
                    System.out.println("Publication Date: " + publicationDate);
                    System.out.println("Image: " + imageUrl);
                    System.out.println("Price: " + price);
                    System.out.println("BookUrl: " + bookUrl);

                    // Create Book object
                    Books book = new Books();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setDescription(description);
                    book.setPublicationDate(publicationDate);

                    // Save or update book in database
                    productDao.saveOrUpdateBook(book);

                    // Create Price object
                    Price bookPrice = new Price();
                    bookPrice.setPrice(price);
                    bookPrice.setImageUrls(imageUrl);
                    bookPrice.setUrls(bookUrl);
                    bookPrice.setBooksId(book.getId());

                    // Create ComparePrice object
                    ComparePrices comparePrice = new ComparePrices();
                    comparePrice.setPrice(price);
                    comparePrice.setImageUrls(imageUrl);
                    comparePrice.setUrls(bookUrl);
                    comparePrice.setBooksId(book.getId());
                    comparePrice.setRetailer("DubrayBooks.ie");

                    // Save Price and ComparePrice objects using ProductDao
                    try {
                        productDao.saveOrUpdatePrice(bookPrice);
                        productDao.saveOrUpdateComparePrice(comparePrice);
                        itemCount++; // Increment item count after successful addition
                    } catch (Exception ex) {
                        System.out.println("Unable to save price or compare price");
                        ex.printStackTrace();
                    }

                } catch (Exception ex) {
                    System.out.println("Dubray Scraper encountered an error");
                    ex.printStackTrace();
                } finally {
                    pageDriver.quit(); // Quit WebDriver instance for individual book page
                }
            }
            driver.quit(); // Quit WebDriver instance for main page
            page++;
        } while (true);
    }

    /**
     * Parses a date string into a LocalDate object.
     *
     * @param dateStr Date string in a specific format
     * @return LocalDate object parsed from dateStr
     */
    public LocalDate parseDate(String dateStr) {
        // Remove ordinal suffixes from date string
        dateStr = dateStr.replaceAll("(\\d+)(st|nd|rd|th)", "$1");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy");
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null; // Handle this case as needed
        }
    }
}
