package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.UserReadBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReadBookRepository extends JpaRepository<UserReadBook, Long> {
    List<UserReadBook> findByUser(User user);
    Optional<UserReadBook> findByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);
}
