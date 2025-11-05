package com.bjournal.bookjournal.model;

import com.bjournal.bookjournal.model.enumerations.Genre;
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
    private Genre genre;

    public Book(String title, String author, String description, String file, Integer pages, Genre genre) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.file = file;
        this.pages = pages;
        this.genre = genre;
    }
}
