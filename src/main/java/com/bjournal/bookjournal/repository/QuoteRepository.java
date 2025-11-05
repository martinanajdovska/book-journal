package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findAllByUserUsernameAndBookId(String username, Long bookId);
}
