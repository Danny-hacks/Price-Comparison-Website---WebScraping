package com.bookmatch.testscraping;

import com.bookmatch.ProductDao;
import com.bookmatch.models.Books;
import com.bookmatch.models.ComparePrices;
import com.bookmatch.models.Price;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

/**
 * The ProductDaoTest class contains JUnit tests for the ProductDao class.
 */
class ProductDaoTest {
    private static ProductDao productDao;
    private static SessionFactory sessionFactory;

    /**
     * Sets up the test environment by creating a Hibernate SessionFactory and initializing the ProductDao.
     */
    @BeforeAll
    static void setUp() {
        Configuration configuration = new Configuration().configure("hibernate-test.cfg.xml");
        sessionFactory = configuration.buildSessionFactory();
        productDao = new ProductDao();
        productDao.sessionFactory = sessionFactory;
    }

    /**
     * Tears down the test environment by closing the Hibernate SessionFactory.
     */
    @AfterAll
    static void tearDown() {
        sessionFactory.close();
    }

    /**
     * Tests the saveOrUpdateBook method of the ProductDao by creating a test Books entity,
     * saving it to the database, and asserting the saved IDs.
     */
    @Test
    void testSaveOrUpdateBook() {
        try {
            // Creating a test Books entity
            Books book = new Books();
            book.setTitle("Test Book");
            book.setAuthor("Test Author");
            book.setDescription("Test Description");
            book.setPublicationDate(LocalDate.now());

            // Save or update the test Books
            productDao.saveOrUpdateBook(book);

            // Asserting that the Books is saved
            Assertions.assertNotNull(book.getId(), "Book ID should not be null after saving.");

            // Deleting the test Books from the database
            cleanUpTestEntities(book);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during the test: " + e.getMessage());
        }
    }

    /**
     * Tests the saveOrUpdateComparePrice method of the ProductDao by creating a test ComparePrices entity,
     * saving it to the database, and asserting the saved IDs.
     */
    @Test
    void testSaveOrUpdateComparePrice() {
        try {
            // Creating a test ComparePrices entity
            ComparePrices comparePrice = new ComparePrices();
            comparePrice.setUrls("http://example.com");
            comparePrice.setBooksId(1);
            comparePrice.setRetailer("Test Retailer");
            comparePrice.setImageUrls("http://example.com/image.jpg");
            comparePrice.setPrice(19.99);

            // Save or update the test ComparePrices
            productDao.saveOrUpdateComparePrice(comparePrice);

            // Asserting that the ComparePrices is saved
            Assertions.assertNotNull(comparePrice.getId(), "ComparePrice ID should not be null after saving.");

            // Deleting the test ComparePrices from the database
            cleanUpTestEntities(comparePrice);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during the test: " + e.getMessage());
        }
    }

    /**
     * Tests the saveOrUpdatePrice method of the ProductDao by creating a test Price entity,
     * saving it to the database, and asserting the saved IDs.
     */
    @Test
    void testSaveOrUpdatePrice() {
        try {
            // Creating a test Price entity
            Price price = new Price();
            price.setUrls("http://example.com");
            price.setBooksId(1);
            price.setImageUrls("http://example.com/image.jpg");
            price.setPrice(19.99);

            // Save or update the test Price
            productDao.saveOrUpdatePrice(price);

            // Asserting that the Price is saved
            Assertions.assertNotNull(price.getId(), "Price ID should not be null after saving.");

            // Deleting the test Price from the database
            cleanUpTestEntities(price);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during the test: " + e.getMessage());
        }
    }

    /**
     * Cleans up the test Books entity by deleting it from the database.
     *
     * @param book The Books entity to be deleted.
     */
    private void cleanUpTestEntities(Books book) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.delete(book);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during cleanup: " + e.getMessage());
        }
    }

    /**
     * Cleans up the test ComparePrices entity by deleting it from the database.
     *
     * @param comparePrice The ComparePrices entity to be deleted.
     */
    private void cleanUpTestEntities(ComparePrices comparePrice) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.delete(comparePrice);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during cleanup: " + e.getMessage());
        }
    }

    /**
     * Cleans up the test Price entity by deleting it from the database.
     *
     * @param price The Price entity to be deleted.
     */
    private void cleanUpTestEntities(Price price) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.delete(price);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception occurred during cleanup: " + e.getMessage());
        }
    }
}
