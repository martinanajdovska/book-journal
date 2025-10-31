package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByAuthor(String author);
    List<Book> findAllByTitleContainingIgnoreCase(String title);
}
