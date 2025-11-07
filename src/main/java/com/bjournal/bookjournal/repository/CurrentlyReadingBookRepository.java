package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.CurrentlyReadingBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentlyReadingBookRepository extends JpaRepository<CurrentlyReadingBook, Long> {
    List<CurrentlyReadingBook> findAllByUserUsername(String username);
    List<CurrentlyReadingBook> findAllByUserUsernameAndBookTitleContainingIgnoreCase(String username, String title);
    boolean existsByUserUsernameAndBookId(String  username, Long  bookId);
    void deleteByUserUsernameAndBookId(String  username, Long  bookId);
    Optional<CurrentlyReadingBook> findByUserUsernameAndBookId(String username, Long  bookId);
}
