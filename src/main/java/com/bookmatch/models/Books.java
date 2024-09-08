package com.bookmatch.models;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entity class representing a book.
 */
@Entity
@Table(name = "books")
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "description")
    private String description;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    /**
     * Get the ID of the book.
     *
     * @return The ID of the book.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Set the ID of the book.
     *
     * @param id The ID of the book.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Get the title of the book.
     *
     * @return The title of the book.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the title of the book.
     *
     * @param title The title of the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the author of the book.
     *
     * @return The author of the book.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Set the author of the book.
     *
     * @param author The author of the book.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get the description of the book.
     *
     * @return The description of the book.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the book.
     *
     * @param description The description of the book.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the publication date of the book.
     *
     * @return The publication date of the book.
     */
    public LocalDate getPublicationDate() {
        return this.publicationDate;
    }

    /**
     * Set the publication date of the book.
     *
     * @param publicationDate The publication date of the book.
     */
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * Override of toString method to provide a string representation of the book object.
     *
     * @return String representation of the book object.
     */
    @Override
    public String toString() {
        return "Books{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", publicationDate=" + publicationDate +
                '}';
    }
}
