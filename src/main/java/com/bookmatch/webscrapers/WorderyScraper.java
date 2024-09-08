package com.bookmatch.webscrapers;

import com.bookmatch.models.ComparePrices;
import com.bookmatch.models.Books;
import com.bookmatch.models.Price;
import com.bookmatch.ProductDao;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * WorderyScraper is a web scraper for extracting book information from the Wordery website.
 * It extends the Thread class to allow concurrent scraping of data.
 */
public class WorderyScraper extends Thread {

    private final FirefoxWebDriverProvider webDriverProvider;
    private ProductDao productDao;
    private static final int MAX_ITEMS = 500; // Maximum number of items to scrape
    private int itemCount = 0; // Counter for the number of items added

    /**
     * Constructs a WorderyScraper instance with the provided WebDriver provider.
     *
     * @param webDriverProvider Provider for obtaining WebDriver instances.
     */
    public WorderyScraper(FirefoxWebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    /**
     * Sets the ProductDao instance to use for saving scraped data.
     *
     * @param productDao ProductDao instance to set.
     */
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Runs the web scraping process for the Wordery website.
     * This method navigates through pages of book listings, extracts details for each book,
     * and saves the information (Book, Price, ComparePrices) to the database using the provided ProductDao.
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
            driver.get("https://wordery.com/historical-romance-FRH?viewBy=grid&resultsPerPage=20&page=" + page + "&leadTime[]=express&formatGroup[]=paperback");

            try {
                Thread.sleep(5000); // Pause to allow page to load
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            System.out.println("Entering Wordery Website page: " + page);
            List<WebElement> bookList = driver.findElements(By.className("o-book-list__book"));

            if (bookList.isEmpty()) {
                break;
            }

            List<String> bookUrls = new ArrayList<>();
            for (WebElement book : bookList) {
                bookUrls.add(book.findElement(By.className("c-book__body")).findElement(By.tagName("a")).getAttribute("href"));
            }

            for (String bookUrl : bookUrls) {
                if (itemCount >= MAX_ITEMS) {
                    System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                    break;
                }

                WebDriver pageDriver = webDriverProvider.getWebDriver();
                pageDriver.get(bookUrl);
                try {
                    Thread.sleep(6000); // Pause to allow book page to load
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                try {
                    // Extracting book details
                    WebElement titleElement = pageDriver.findElement(By.xpath("/html/body/div[4]/main/div[1]/div/div[1]/h1"));
                    String title = titleElement.getText().replace("(Paperback)", "").trim();
                    String author = pageDriver.findElement(By.xpath("/html/body/div[4]/main/div[1]/div/div[1]/p/a")).getText();
                    String description = pageDriver.findElement(By.xpath("/html/body/div[4]/main/div[3]/div/div[1]/div[1]/div/div/p[1]")).getText();
                    String publicationDateStr = null;
                    try {
                        publicationDateStr = pageDriver.findElement(By.xpath("/html/body/div[4]/main/div[1]/div/div[1]/p/span")).getAttribute("content");
                    } catch (Exception e) {
                        System.out.println("Publication date not found, setting today's date");
                    }
                    WebElement imageElement = pageDriver.findElement(By.xpath("/html/body/div[4]/main/div[1]/div/div[1]/div/div[1]/div[1]/a/div/img"));
                    String imageUrl = imageElement.getAttribute("src");

                    // Parsing publication date
                    LocalDate publicationDate = publicationDateStr != null ? Date.valueOf(publicationDateStr.substring(0, 10)).toLocalDate() : LocalDate.now();

                    // Setting price temporarily as 0.0
                    double price = 0.0;

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

                    // Creating ComparePrices object
                    ComparePrices comparePrice = new ComparePrices();
                    comparePrice.setPrice(price);
                    comparePrice.setImageUrls(imageUrl);
                    comparePrice.setUrls(bookUrl);
                    comparePrice.setBooksId(book.getId());
                    comparePrice.setRetailer("Wordery.com");

                    // Saving the Price and ComparePrices objects using the provided ProductDao
                    try {
                        productDao.saveOrUpdatePrice(bookPrice);
                        productDao.saveOrUpdateComparePrice(comparePrice);
                        itemCount++; // Increment the item count after successful addition
                    } catch (Exception ex) {
                        System.out.println("Unable to save price or compare price");
                        ex.printStackTrace();
                    }

                } catch (Exception ex) {
                    System.out.println("WorderyScraper broke");
                    ex.printStackTrace();
                } finally {
                    pageDriver.quit();
                }
            }
            driver.quit();
            page++;
        } while (true);
    }
}
