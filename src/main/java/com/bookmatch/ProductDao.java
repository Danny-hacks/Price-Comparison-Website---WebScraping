package com.bookmatch;

import com.bookmatch.models.Books;
import com.bookmatch.models.ComparePrices;
import com.bookmatch.models.Price;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

/**
 * Data Access Object (DAO) for managing product-related operations using Hibernate.
 */
public class ProductDao {

    /** The Hibernate session factory for managing database connections. */
    public SessionFactory sessionFactory;

    /**
     * Saves or updates a book in the database.
     *
     * @param book The book object to save or update.
     * @throws Exception If multiple books with the same title and author are found.
     */
    public void saveOrUpdateBook(Books book) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String queryStr = "from Books where title=:title and author=:author";
            List<Books> bookList = session.createQuery(queryStr, Books.class)
                    .setParameter("title", book.getTitle())
                    .setParameter("author", book.getAuthor())
                    .getResultList();

            if (bookList.size() == 1) {
                Books existingBook = bookList.get(0);
                if (!book.equals(existingBook)) {
                    book.setId(existingBook.getId());
                    session.merge(book);
                    System.out.println("Book updated with ID: " + book.getId());
                } else {
                    System.out.println("No changes detected for Book with ID: " + existingBook.getId());
                }
            } else if (bookList.isEmpty()) {
                session.save(book);
                System.out.println("Book added with ID: " + book.getId());
            } else {
                throw new Exception("Multiple books with the same title and author found");
            }

            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Saves or updates a compare price entry in the database.
     *
     * @param comparePrice The compare price object to save or update.
     * @throws Exception If multiple compare prices with the same URL, retailer, and image URLs are found.
     */
    public void saveOrUpdateComparePrice(ComparePrices comparePrice) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String queryStr = "from ComparePrices where urls=:urls and booksId=:booksId and retailer=:retailer and imageUrls=:imageUrls";
            List<ComparePrices> comparePriceList = session.createQuery(queryStr, ComparePrices.class)
                    .setParameter("urls", comparePrice.getUrls())
                    .setParameter("booksId", comparePrice.getBooksId())
                    .setParameter("retailer", comparePrice.getRetailer())
                    .setParameter("imageUrls", comparePrice.getImageUrls())
                    .getResultList();

            if (comparePriceList.size() == 1) {
                ComparePrices existingComparePrice = comparePriceList.get(0);
                if (!comparePrice.equals(existingComparePrice)) {
                    comparePrice.setId(existingComparePrice.getId());
                    session.merge(comparePrice);
                    System.out.println("ComparePrice updated with ID: " + comparePrice.getId());
                } else {
                    System.out.println("No changes detected for ComparePrice with ID: " + existingComparePrice.getId());
                }
            } else if (comparePriceList.isEmpty()) {
                session.save(comparePrice);
                System.out.println("ComparePrice added with ID: " + comparePrice.getId());
            } else {
                throw new Exception("Multiple ComparePrices with the same URL, retailer, and image URLs found");
            }

            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Saves or updates a price entry in the database.
     *
     * @param price The price object to save or update.
     * @throws Exception If multiple prices with the same URL and image URLs are found.
     */
    public void saveOrUpdatePrice(Price price) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String queryStr = "from Price where urls=:urls and booksId=:booksId and imageUrls=:imageUrls";
            List<Price> priceList = session.createQuery(queryStr, Price.class)
                    .setParameter("urls", price.getUrls())
                    .setParameter("booksId", price.getBooksId())
                    .setParameter("imageUrls", price.getImageUrls())
                    .getResultList();

            if (priceList.size() == 1) {
                Price existingPrice = priceList.get(0);
                if (!price.equals(existingPrice)) {
                    price.setId(existingPrice.getId());
                    session.merge(price);
                    System.out.println("Price updated with ID: " + price.getId());
                } else {
                    System.out.println("No changes detected for Price with ID: " + existingPrice.getId());
                }
            } else if (priceList.isEmpty()) {
                session.save(price);
                System.out.println("Price added with ID: " + price.getId());
            } else {
                throw new Exception("Multiple Prices with the same URL and image URLs found");
            }

            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Initializes the Hibernate session factory using the configuration file 'hibernate.cfg.xml'.
     */
    public void init() {
        try {
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
            standardServiceRegistryBuilder.configure("hibernate.cfg.xml");

            StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
            try {
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            } catch (Exception ex) {
                System.out.println("Session Factory build failed" + ex);
                ex.printStackTrace();
                StandardServiceRegistryBuilder.destroy(registry);
            }

            System.out.println("Session Factory built successfully");
        } catch (Throwable ex) {
            ex.printStackTrace();
            System.out.println("SessionFactory creation failed." + ex);
        }
    }
}
