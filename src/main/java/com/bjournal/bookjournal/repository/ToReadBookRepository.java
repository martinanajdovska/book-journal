package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.ToReadBook;
import com.bjournal.bookjournal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToReadBookRepository extends JpaRepository<ToReadBook, Long> {
    List<ToReadBook> findAllByUser(User user);
    Optional<ToReadBook> findByUserAndBook(User user, Book book);
    void deleteToReadBookByUserAndBook(User user, Book book);
    boolean existsToReadBookByUserAndBook(User user, Book book);
}
