package com.bookmatch.webscrapers;

import com.bookmatch.models.ComparePrices;
import com.bookmatch.models.Books;
import com.bookmatch.models.Price;
import com.bookmatch.ProductDao;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * OwlsNestScraper is a web scraper for extracting book information from the Owl's Nest Books website.
 * It extends the Thread class to allow concurrent scraping of data.
 */
public class OwlsNestScraper extends Thread {

    private final FirefoxWebDriverProvider webDriverProvider; // Provider for WebDriver instance
    public ProductDao productDao; // Data access object for saving scraped data
    private static final int MAX_ITEMS = 200; // Maximum number of items to scrape
    private int itemCount = 0; // Counter for the number of items added

    /**
     * Constructs an OwlsNestScraper with a given WebDriver provider.
     *
     * @param webDriverProvider Provider for obtaining WebDriver instances.
     */
    public OwlsNestScraper(FirefoxWebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    /**
     * Sets the ProductDao instance for saving scraped data.
     *
     * @param productDao ProductDao instance to be set.
     */
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Starts the scraping process for extracting book information.
     * It navigates through pages of Owl's Nest Books website, extracts book details,
     * and saves them using the provided ProductDao instance.
     */
    @Override
    public void run() {
        int page = 1; // Page number for navigation
        do {
            if (itemCount >= MAX_ITEMS) {
                System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                break;
            }

            WebDriver driver = webDriverProvider.getWebDriver(); // Obtain WebDriver instance
            driver.get("https://owlsnestbooks.com/browse/filter/b/rankcaranked/s/fic/fic014/fic027/f/pb/l/200/v/popularity/x/LVvXTG8EsDDU");

            try {
                Thread.sleep(9000); // Wait for page to load
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            System.out.println("Entering OwlsNest Website page: " + page);
            List<WebElement> bookList = driver.findElements(By.xpath("//a[@class='nav']")); // Find book links

            if (bookList.isEmpty()) {
                break;
            }

            List<String> bookUrls = new ArrayList<>();
            for (WebElement book : bookList) {
                bookUrls.add(book.getAttribute("href")); // Collect book URLs
            }

            for (String bookUrl : bookUrls) {
                if (itemCount >= MAX_ITEMS) {
                    System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                    break;
                }

                WebDriver pageDriver = webDriverProvider.getWebDriver();
                pageDriver.get(bookUrl);

                try {
                    Thread.sleep(9000); // Wait for page to load
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                try {
                    // Extract book details
                    WebElement titleElement = pageDriver.findElement(By.xpath("/html/body/div[1]/div/main/div[7]/div/section/main/div[1]/div[2]/h2"));
                    String title = titleElement.getText().replace("A Novel", "").trim();
                    String author = pageDriver.findElement(By.xpath("/html/body/div[1]/div/main/div[7]/div/section/main/div[1]/div[2]/strong/span/div")).getText().replace("maple leaf from the flag of Canada", "").trim();
                    String description;
                    try {
                        // Try to get description using primary XPath
                        WebElement descriptionElement = pageDriver.findElement(By.xpath("/html/body/div[1]/div/main/div[7]/div/section/main/div[1]/div[2]/div[5]/div[3]/div[2]/div/div/span/div[1]/b[1]"));
                        description = descriptionElement.getText().split("\\r?\\n|\\.[1]")[0].trim();
                    } catch (Exception e) {
                        // Use alternative XPath if primary fails
                        WebElement descriptionElement = pageDriver.findElement(By.xpath("/html/body/div[1]/div/main/div[7]/div/section/main/div[1]/div[2]/div[5]/div[4]/div[2]/div/div/span/div[1]/p"));
                        description = descriptionElement.getText().split("\\r?\\n|\\.[1]")[0].trim();
                    }
                    String publicationDateStr = pageDriver.findElement(By.xpath("/html/body/div[1]/div/main/div[7]/div/section/main/div[1]/div[2]/div[5]/button[1]/span[2]")).getText();
                    String imageUrl = pageDriver.findElement(By.xpath("/html/body/div[1]/div/main/div[7]/div/section/main/div[1]/div[1]/div[2]/div/div[2]/div/img")).getAttribute("src");
                    String priceStr = pageDriver.findElement(By.xpath("/html/body/div[1]/div/main/div[7]/div/section/aside/div/div[7]/div/div[1]/span/strong[1]")).getText().replace("$", "").trim();
                    double price = Double.parseDouble(priceStr);

                    // Parsing publication date
                    String publicationDateCleanStr = publicationDateStr.replace("Published: ", "").trim();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                    LocalDate publicationDate = LocalDate.parse(publicationDateCleanStr, formatter);

                    // Displaying extracted information
                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("Description: " + description);
                    System.out.println("Publication Date: " + publicationDate);
                    System.out.println("Image: " + imageUrl);
                    System.out.println("Price: " + price);
                    System.out.println("BookUrl: " + bookUrl);

                    // Creating Book object
                    Books book = new Books();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setDescription(description);
                    book.setPublicationDate(publicationDate);

                    // Save or update the book in the database
                    productDao.saveOrUpdateBook(book);

                    // Creating Price object
                    Price bookPrice = new Price();
                    bookPrice.setPrice(price);
                    bookPrice.setImageUrls(imageUrl);
                    bookPrice.setUrls(bookUrl);
                    bookPrice.setBooksId(book.getId());

                    // Creating ComparePrice object
                    ComparePrices comparePrice = new ComparePrices();
                    comparePrice.setPrice(price);
                    comparePrice.setImageUrls(imageUrl);
                    comparePrice.setUrls(bookUrl);
                    comparePrice.setBooksId(book.getId());
                    comparePrice.setRetailer("OwlsNestBooks.com");

                    // Save the Price and ComparePrice objects using the provided ProductDao
                    try {
                        productDao.saveOrUpdatePrice(bookPrice);
                        productDao.saveOrUpdateComparePrice(comparePrice);
                        itemCount++; // Increment the item count after successful addition
                    } catch (Exception ex) {
                        System.out.println("Unable to save price or compare price");
                        ex.printStackTrace();
                    }

                } catch (Exception ex) {
                    System.out.println("OwlsNest Scraper Broke");
                    ex.printStackTrace();
                } finally {
                    pageDriver.quit();
                }
            }
            driver.quit(); // Quit WebDriver instance
            page++; // Move to the next page
        } while (true);
    }
}
