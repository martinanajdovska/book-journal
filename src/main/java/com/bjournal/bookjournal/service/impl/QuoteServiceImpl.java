package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Quote;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.QuoteNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.repository.QuoteRepository;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.QuoteService;
import com.bjournal.bookjournal.service.UserReadBookService;
import com.bjournal.bookjournal.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserService userService;
    private final BookService bookService;
    private final UserReadBookService userReadBookService;

    public QuoteServiceImpl(QuoteRepository quoteRepository, UserService userService, BookService bookService, UserReadBookService userReadBookService) {
        this.quoteRepository = quoteRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.userReadBookService = userReadBookService;
    }

    @Override
    public void add(String username, Long bookId, String text) {
        if (username == null || bookId == null || text == null || text.isBlank()) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        User user = this.userService.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
        Book book = this.bookService.findById(bookId).orElseThrow(()->new BookNotFoundException(bookId));
        UserReadBook userReadBook = this.userReadBookService.findLastByUserUsernameAndBookId(username,bookId).orElseThrow(()-> new UserReadBookNotFoundException(username,bookId));

        this.quoteRepository.save(new Quote(book,user,String.format("\"%s\"", text)));
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        this.quoteRepository.deleteById(id);
    }

    @Override
    public Optional<Quote> update(Long id, String text) {
        if (id == null || text == null || text.isBlank()) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        Quote quote = this.quoteRepository.findById(id).orElseThrow(()->new QuoteNotFoundException(id));
        quote.setText(text);
        this.quoteRepository.save(quote);
        return Optional.of(quote);
    }

    @Override
    public List<Quote> findAllByUsernameAndBookId(String username, Long bookId) {
        return this.quoteRepository.findAllByUserUsernameAndBookId(username, bookId);
    }
}
