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
    public List<Review> findAllByBook(Book book) {
        return this.reviewRepository.findAllByBook(book);
    }

    @Override
    public void addReview(String text, String username, Long bookId) {
        User user = this.userService.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));
        Book book = this.bookService.findById(bookId).orElseThrow(()-> new BookNotFoundException(bookId));
        // if user hasn't read this book then they can't add a review
        UserReadBook userReadBook = this.userReadBookService.findByUserAndBook(username,book).orElseThrow(()-> new UserReadBookNotFoundException(user,book));

        if (text==null || text.isBlank()){
            throw new IllegalArgumentException("review is empty");
        }

        Review review = new Review(text, book, user);
        reviewRepository.save(review);
    }

}
