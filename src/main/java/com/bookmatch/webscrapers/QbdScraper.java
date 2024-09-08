package com.bookmatch.webscrapers;

import com.bookmatch.models.ComparePrices;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.bookmatch.models.Books;
import com.bookmatch.models.Price;
import com.bookmatch.ProductDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * QbdScraper is a web scraper for extracting book information from the QBD Books website.
 * It extends the Thread class to allow concurrent scraping of data.
 */
public class QbdScraper extends Thread {

    private final FirefoxWebDriverProvider webDriverProvider;
    public ProductDao productDao;
    private static final int MAX_ITEMS = 500; // Maximum number of items to scrape
    private int itemCount = 0; // Counter for the number of items added

    /**
     * Constructor for QbdScraper.
     * @param webDriverProvider Provides WebDriver instances for scraping.
     */
    public QbdScraper(FirefoxWebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    /**
     * Sets the ProductDao instance to use for saving scraped data.
     * @param productDao The ProductDao instance to use.
     */
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Starts the scraping process.
     * This method is overridden from Thread class and handles the main scraping logic.
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
            driver.get("https://www.qbd.com.au/fiction/romance/best-sellers/" + page);

            try {
                Thread.sleep(3000); // Pause to allow page to load
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Entering Qbd Books Website page: " + page);
            List<WebElement> bookList = driver.findElements(By.className("q2k21pt"));

            if (bookList.isEmpty()) {
                break;
            }

            List<String> bookUrls = new ArrayList<>();
            for (WebElement book : bookList) {
                bookUrls.add(book.findElement(By.className("q2k21ptc")).findElement(By.tagName("a")).getAttribute("href"));
            }

            for (String bookUrl : bookUrls) {
                if (itemCount >= MAX_ITEMS) {
                    System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                    break;
                }

                WebDriver pageDriver = webDriverProvider.getWebDriver();
                pageDriver.get(bookUrl);
                try {
                    Thread.sleep(4000); // Pause to allow page to load
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    // Extracting book details
                    WebElement titleElement = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/h1"));
                    String title = titleElement.getText().replace("Collector's Edition", "").trim();
                    String author = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/form/div[1]/div[2]/div[1]/span/a")).getText();
                    WebElement descriptionElement = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/form/div[1]/div[2]/div[7]"));
                    String description = descriptionElement.getText().split("\\r?\\n|\\.[1]")[0].trim();
                    String publicationDateStr = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/form/div[1]/div[2]/div[3]/span")).getText();
                    String imageUrl = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/form/div[1]/div[1]/div")).findElement(By.tagName("img")).getAttribute("src");
                    String priceStr = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/form/div[1]/div[3]/div[1]/div/div[1]/span")).getText().replace("$", "").trim();
                    double price = Double.parseDouble(priceStr);

                    // Parsing publication date
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd / MM / yyyy");
                    LocalDate publicationDate = LocalDate.parse(publicationDateStr, formatter);

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
                    comparePrice.setRetailer("QBD.com");

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
                    System.out.println("QBD Scraper Broke");
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
