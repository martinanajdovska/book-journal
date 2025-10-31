package com.bjournal.bookjournal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String description;
    private String file;
    private Integer pages;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<UserReadBook> userReadBooks;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public Book(String title, String author, String description, String file, Integer pages) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.file = file;
        this.pages = pages;
        this.userReadBooks = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }
}
