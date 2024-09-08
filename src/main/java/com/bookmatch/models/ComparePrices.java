package com.bookmatch.models;

import javax.persistence.*;

/**
 * Entity class representing the table 'compare_prices' for storing price and retailer information of books.
 */
@Entity
@Table(name = "compare_prices")
public class ComparePrices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "price")
    private Double price;

    @Column(name = "image_urls")
    private String imageUrls;

    @Column(name = "urls")
    private String urls;

    @Column(name = "books_id")
    private Integer booksId;

    @Column(name = "retailer")
    private String retailer;

    /**
     * Get the ID of the price comparison entry.
     *
     * @return The ID of the price comparison entry.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Set the ID of the price comparison entry.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the price of the book from this comparison entry.
     *
     * @return The price of the book.
     */
    public Double getPrice() {
        return this.price;
    }

    /**
     * Set the price of the book for this comparison entry.
     *
     * @param price The price to set.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Get the image URLs associated with the book from this comparison entry.
     *
     * @return The image URLs.
     */
    public String getImageUrls() {
        return this.imageUrls;
    }

    /**
     * Set the image URLs associated with the book for this comparison entry.
     *
     * @param imageUrls The image URLs to set.
     */
    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Get the URLs where the book can be purchased from this comparison entry.
     *
     * @return The purchase URLs.
     */
    public String getUrls() {
        return this.urls;
    }

    /**
     * Set the URLs where the book can be purchased for this comparison entry.
     *
     * @param urls The purchase URLs to set.
     */
    public void setUrls(String urls) {
        this.urls = urls;
    }

    /**
     * Get the ID of the book associated with this comparison entry.
     *
     * @return The ID of the book.
     */
    public Integer getBooksId() {
        return this.booksId;
    }

    /**
     * Set the ID of the book associated with this comparison entry.
     *
     * @param booksId The ID of the book to set.
     */
    public void setBooksId(Integer booksId) {
        this.booksId = booksId;
    }

    /**
     * Get the retailer where the book can be purchased from this comparison entry.
     *
     * @return The retailer name.
     */
    public String getRetailer() {
        return this.retailer;
    }

    /**
     * Set the retailer where the book can be purchased for this comparison entry.
     *
     * @param retailer The retailer name to set.
     */
    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    /**
     * Returns a string representation of the ComparePrices object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "ComparePrices{" +
                "id=" + id +
                ", price=" + price +
                ", imageUrls='" + imageUrls + '\'' +
                ", urls='" + urls + '\'' +
                ", booksId=" + booksId +
                ", retailer='" + retailer + '\'' +
                '}';
    }
}
