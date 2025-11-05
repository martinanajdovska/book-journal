package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.UserReadBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReadBookRepository extends JpaRepository<UserReadBook, Long> {
    List<UserReadBook> findAllByUserUsername(String username);
    Optional<UserReadBook> findByUserUsernameAndBookId(String username, Long book);
    List<UserReadBook> findAllByUserUsernameAndBookTitleContainingIgnoreCase(String username, String title);
}
