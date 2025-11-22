package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.model.Review;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.repository.ReviewRepository;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.UserReadBookService;
import com.bjournal.bookjournal.service.ReviewService;
import com.bjournal.bookjournal.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserReadBookService userReadBookService;
    private final UserService userService;
    private final BookService bookService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserReadBookService userReadBookService, UserService userService, BookService bookService) {
        this.reviewRepository = reviewRepository;
        this.userReadBookService = userReadBookService;
        this.userService = userService;
        this.bookService = bookService;
    }

    @Override
    public List<Review> findAllByUser(User user) {
        return this.reviewRepository.findAllByUser(user);
    }

    @Override
    public List<Review> findAllByBookId(Long bookId) {
        return this.reviewRepository.findAllByBookId(bookId);
    }

    @Override
    public void addReview(String text, String username, Long bookId, Float rating) {
        if ((text==null || text.isBlank()) && rating == null) {
            throw new IllegalArgumentException("Review is empty");
        }

        User user = this.userService.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));
        Book book = this.bookService.findById(bookId).orElseThrow(()-> new BookNotFoundException(bookId));
        // if user hasn't read this book then they can't add a review
        UserReadBook userReadBook = this.userReadBookService.findLastByUserUsernameAndBookId(username,bookId).orElseThrow(()-> new UserReadBookNotFoundException(username,bookId));

        Review review = new Review(text, book, user, rating);
        reviewRepository.save(review);
    }

    @Override
    public Float averageRatingByBookId(Long bookId) {
        return this.reviewRepository.averageRatingByBookId(bookId);
    }

}
