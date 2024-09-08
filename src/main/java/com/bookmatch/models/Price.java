package com.bookmatch.models;

import javax.persistence.*;

/**
 * Represents the price details of a book.
 */
@Entity
@Table(name = "price")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "price")
    private Double price;

    @Column(name = "image_urls")
    private String imageUrls;

    @Column(name = "urls")
    private String urls;

    @Column(name = "books_id")
    private Integer booksId;

    /**
     * Retrieves the ID of the price entry.
     *
     * @return The ID of the price entry.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the ID of the price entry.
     *
     * @param id The ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retrieves the price of the book.
     *
     * @return The price of the book.
     */
    public Double getPrice() {
        return this.price;
    }

    /**
     * Sets the price of the book.
     *
     * @param price The price to set.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Retrieves the image URLs associated with the price entry.
     *
     * @return The image URLs.
     */
    public String getImageUrls() {
        return this.imageUrls;
    }

    /**
     * Sets the image URLs associated with the price entry.
     *
     * @param imageUrls The image URLs to set.
     */
    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Retrieves the URLs where the book can be purchased.
     *
     * @return The URLs for purchasing the book.
     */
    public String getUrls() {
        return this.urls;
    }

    /**
     * Sets the URLs where the book can be purchased.
     *
     * @param urls The URLs to set.
     */
    public void setUrls(String urls) {
        this.urls = urls;
    }

    /**
     * Retrieves the ID of the book associated with this price entry.
     *
     * @return The ID of the associated book.
     */
    public Integer getBooksId() {
        return this.booksId;
    }

    /**
     * Sets the ID of the book associated with this price entry.
     *
     * @param booksId The ID of the associated book to set.
     */
    public void setBooksId(Integer booksId) {
        this.booksId = booksId;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", price=" + price +
                ", imageUrls='" + imageUrls + '\'' +
                ", urls='" + urls + '\'' +
                ", booksId=" + booksId +
                '}';
    }
}
