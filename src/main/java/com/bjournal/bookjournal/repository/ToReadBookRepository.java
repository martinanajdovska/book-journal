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
    List<ToReadBook> findAllByUserUsername(String username);
    Optional<ToReadBook> findByUserUsernameAndBookId(String username, Long bookId);
    void deleteToReadBookByUserAndBook(User user, Book book);
    boolean existsToReadBookByUserAndBook(User user, Book book);
    List<ToReadBook> findAllByUserUsernameAndBookTitleContainingIgnoreCase(String username, String title);

    List<ToReadBook> user(User user);
}
