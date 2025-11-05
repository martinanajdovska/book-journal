package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Quote;
import com.bjournal.bookjournal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findAllByUserAndBook(User user, Book book);
}
