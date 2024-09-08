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
 * HpbScraper is a web scraper for extracting book information from the Half Price Books website.
 * It extends the Thread class to allow concurrent scraping of data.
 */
public class HpbScraper extends Thread {

    private final FirefoxWebDriverProvider webDriverProvider;
    private ProductDao productDao;
    private static final int MAX_ITEMS = 500; // Maximum number of items to scrape
    private int itemCount = 0; // Counter for the number of items added

    /**
     * Constructor for HpbScraper.
     *
     * @param webDriverProvider Provides WebDriver instances for scraping.
     */
    public HpbScraper(FirefoxWebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    /**
     * Sets the ProductDao instance used for saving scraped data.
     *
     * @param productDao The ProductDao instance.
     */
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Starts the scraping process.
     */
    @Override
    public void run() {
        int page = 2;
        do {
            if (itemCount >= MAX_ITEMS) {
                System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                break;
            }

            WebDriver driver = webDriverProvider.getWebDriver();
            driver.get("https://www.hpb.com/books/categories/romance/historical?prefn1=Format&prefv1=Paperback&prefn2=instorePickUpAvailableStores&prefv2=HPB-001&prefn3=subjectLevel4&prefv3=Regency%7cMedieval&sz=80&srule=most-popular#pg" + page);

            try {
                Thread.sleep(3000); // Sleep to allow page to load
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            System.out.println("Entering Hpb Website page: " + page);
            List<WebElement> bookList = driver.findElements(By.className("product-grid-element"));

            if (bookList.isEmpty()) {
                break;
            }

            List<String> bookUrls = new ArrayList<>();
            for (WebElement book : bookList) {
                bookUrls.add(book.findElement(By.className("product-tile")).findElement(By.className("image-container")).findElement(By.tagName("a")).getAttribute("href"));
            }

            for (String bookUrl : bookUrls) {
                if (itemCount >= MAX_ITEMS) {
                    System.out.println("Reached the maximum number of items: " + MAX_ITEMS);
                    break;
                }

                WebDriver pageDriver = webDriverProvider.getWebDriver();
                pageDriver.get(bookUrl);
                try {
                    Thread.sleep(4000); // Sleep to allow page to load
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                try {
                    WebElement titleElement = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[2]/div[1]/div/h1"));
                    String title = titleElement.getText();
                    String author = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[2]/div[1]/div/span/a/span")).getText();
                    WebElement descriptionElement = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/div[1]")).findElement(By.className("multi-collapse"));
                    String description = descriptionElement.getText().split("\\r?\\n|\\.")[0].trim(); // Split by newline or period and take the first part
                    String publicationDateStr = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/div[2]/div/div/div/ul/li[7]/span[2]")).getText();
                    String imageUrl = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[1]/div/div/div/div")).findElement(By.tagName("img")).getAttribute("src");
                    String priceStr = pageDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[3]/div[3]/div[1]/div/div/div/span/span/span")).getText().replace("$", "").trim();
                    double price = Double.parseDouble(priceStr);

                    // Parsing publication date
                    LocalDate publicationDate;
                    if (publicationDateStr.matches("\\d{4}")) { // If only the year is provided
                        publicationDate = LocalDate.of(Integer.parseInt(publicationDateStr), 1, 1);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        publicationDate = LocalDate.parse(publicationDateStr, formatter);
                    }

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
                    comparePrice.setRetailer("HPB.com");

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
                    System.out.println("HPB Scraper Broke");
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
