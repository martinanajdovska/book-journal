package com.bjournal.bookjournal.repository;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Review;
import com.bjournal.bookjournal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUser(User user);
    List<Review> findAllByBookId(Long bookId);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id=?1")
    Float averageRatingByBookId(Long bookId);
}
