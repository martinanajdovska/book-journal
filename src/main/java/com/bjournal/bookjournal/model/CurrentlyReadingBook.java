package com.bjournal.bookjournal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "currently_reading_book",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
public class CurrentlyReadingBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startedDate;
    private Float progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public CurrentlyReadingBook(LocalDate startedDate, Float progress, Book book, User user) {
        this.startedDate = startedDate;
        this.progress = progress;
        this.book = book;
        this.user = user;
    }
}
