package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Review;
import com.bjournal.bookjournal.model.User;

import java.util.List;

public interface ReviewService {
    List<Review> findAllByUser(User user);
    List<Review> findAllByBook(Book book);
    void addReview(String review, String username, Long bookId);
}
