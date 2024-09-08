package com.bookmatch.webscrapers;

import com.bookmatch.models.ComparePrices;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.bookmatch.models.Books;
import com.bookmatch.models.Price;
import com.bookmatch.ProductDao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * WaterStonesScraper is a web scraper for extracting book information from the Water Stones website.
 * It extends the Thread class to allow concurrent scraping of data.
 */
public class WaterStonesScraper extends Thread {

    private final FirefoxWebDriverProvider webDriverProvider;
    private ProductDao productDao;
    private static final int MAX_ITEMS = 500; // Maximum number of items to scrape
    private int itemCount = 0; // Counter for the number of items added

    /**
     * Constructor for WaterStonesScraper.
     * @param webDriverProvider The provider for the WebDriver instance.
     */
    public WaterStonesScraper(FirefoxWebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    /**
     * Sets the ProductDao for saving scraped data.
     * @param productDao The ProductDao instance to be set.
     */
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Starts scraping data from Water Stones website.
     * This method navigates through pages and extracts book information until MAX_ITEMS is reached.
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
            driver.get("https://www.waterstones.com/category/romantic-fiction/historical-romance/sortmode/bestselling/format/17/page/" + page);

            try {
                Thread.sleep(5000); // Waiting for the page to load
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            System.out.println("Entering Waterstones Website page: " + page);
            List<WebElement> bookList = driver.findElements(By.className("book-preview-grid-item"));

            if (bookList.isEmpty()) {
                break;
            }

            List<String> bookUrls = new ArrayList<>();
            for (WebElement book : bookList) {
                bookUrls.add(book.findElement(By.className("info-wrap")).findElement(By.className("title-wrap")).findElement(By.tagName("a")).getAttribute("href"));
            }

            for (String bookUrl : bookUrls) {
                if (itemCount >= MAX_ITEMS) {
                    System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                    break;
                }

                WebDriver pageDriver = webDriverProvider.getWebDriver();
                pageDriver.get(bookUrl);
                try {
                    Thread.sleep(5000); // Waiting for the book page to load
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                try {
                    // Extracting book details
                    WebElement titleElement = pageDriver.findElement(By.xpath("//*[@id=\"scope_book_title\"]"));
                    String title = titleElement.getText().replace("(Paperback)", "").trim();
                    String author = pageDriver.findElement(By.cssSelector("span[itemprop='author']")).getText();
                    String description = pageDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/section[2]/div[2]/div[1]/div[1]/div[1]/p")).getText();
                    String publicationDateStr = pageDriver.findElement(By.cssSelector("meta[itemprop='datePublished']")).getAttribute("content");
                    String imageUrl = pageDriver.findElement(By.xpath("//*[@id=\"scope_book_image\"]")).getAttribute("src");
                    String priceStr = pageDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/section[1]/div[2]/div[2]/div/div[1]/div/div[1]/div[1]/b")).getText().replace("£", "").trim();
                    double price = Double.parseDouble(priceStr);

                    // Displaying extracted information
                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("Description: " + description);
                    System.out.println("Publication Date: " + publicationDateStr);
                    System.out.println("Image: " + imageUrl);
                    System.out.println("Price: " + price);
                    System.out.println("BookUrl: " + bookUrl);

                    // Creating Book object
                    Books book = new Books();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setDescription(description);
                    book.setPublicationDate(Date.valueOf(publicationDateStr).toLocalDate());

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
                    comparePrice.setRetailer("Waterstones.com");

                    // Saving the Price and ComparePrice objects using the provided ProductDao
                    try {
                        productDao.saveOrUpdatePrice(bookPrice);
                        productDao.saveOrUpdateComparePrice(comparePrice);
                        itemCount++; // Increment the item count after successful addition
                    } catch (Exception ex) {
                        System.out.println("Unable to save price or compare price");
                        ex.printStackTrace();
                    }

                } catch (Exception ex) {
                    System.out.println("Error extracting book details from: " + bookUrl);
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
