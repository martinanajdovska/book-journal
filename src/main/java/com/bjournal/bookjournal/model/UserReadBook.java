package com.bjournal.bookjournal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class UserReadBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate addedDate;
    private LocalDate startedDate;
    private LocalDate finishedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    public UserReadBook(LocalDate addedDate, User user, Book book) {
        this.addedDate = addedDate;
        this.startedDate = null;
        this.finishedDate = null;
        this.user = user;
        this.book = book;
    }
}
