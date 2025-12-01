package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook,Long> {
    List<BorrowedBook> findAllByUserUsername(String username);
}
