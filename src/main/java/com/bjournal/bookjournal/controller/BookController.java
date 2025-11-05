package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.*;
import com.bjournal.bookjournal.model.enumerations.Genre;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserReadBookService userReadBookService;
    private final ToReadBookService toReadBookService;
    private final QuoteService quoteService;
    private final CurrentlyReadingBookService currentlyReadingBookService;

    public BookController(BookService bookService, ReviewService reviewService, UserReadBookService userReadBookService, ToReadBookService toReadBookService, QuoteService quoteService, CurrentlyReadingBookService currentlyReadingBookService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.userReadBookService = userReadBookService;
        this.toReadBookService = toReadBookService;
        this.quoteService = quoteService;
        this.currentlyReadingBookService = currentlyReadingBookService;
    }

    @GetMapping("/add")
    public String getAddBookPage(Model model) {
        List<Genre> genres = Arrays.stream(Genre.values()).sorted(Comparator.comparing(Enum::name)).toList();
        model.addAttribute("genres", genres);
        return "book-form";
    }

    @PostMapping("/add")
    public String addBook(@RequestParam(required = true) String title, @RequestParam(required = true) String author,
                          @RequestParam(required = true) String description, @RequestParam(required = true) MultipartFile file,
                          @RequestParam(required = true) Integer pages, @RequestParam(required = true) Genre genre, Model model) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            model.addAttribute("error", "Only JPEG or PNG images are allowed");
            model.addAttribute("hasError", true);
            return "book-form";
        }
        try {
            this.bookService.add(title, author, description, file, pages, genre);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("hasError", true);
            return "book-form";
        } catch (IOException e) {
            model.addAttribute("error", "Error while saving the image");
            model.addAttribute("hasError", true);
            return "book-form";
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String getEditBookPage(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.findById(id);
        List<Genre> genres = Arrays.stream(Genre.values()).sorted(Comparator.comparing(Enum::name)).toList();
        model.addAttribute("genres", genres);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book-form";
        }
        model.addAttribute("error", "Book not found");
        return "error-page";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, @RequestParam(required = true) String title,
                           @RequestParam(required = true) String author, @RequestParam(required = true) String description,
                           @RequestParam(required = false) MultipartFile file, @RequestParam(required = true) Integer pages,
                           @RequestParam(required = true) Genre genre, Model model) {
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                model.addAttribute("error", "Only JPEG or PNG images are allowed");
                model.addAttribute("hasError", true);
                return "book-form";
            }
        }

        try {
            this.bookService.update(id, title, author, description, file, pages, genre);
        } catch (BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        } catch (IllegalArgumentException e) {
            // no need to check if it exists because it didn't throw an exception
            Book book = bookService.findById(id).get();
            model.addAttribute("book", book);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("hasError", true);
            return "book-form";
        } catch (IOException e) {
            Book book = bookService.findById(id).get();
            model.addAttribute("book", book);
            model.addAttribute("error", "Error while saving the image");
            model.addAttribute("hasError", true);
            return "book-form";
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, Model model) {
        try {
            this.bookService.deleteById(id);
        } catch (BookNotFoundException e) {
            model.addAttribute("error", "Book not found");
            return "error-page";
        }
        return "redirect:/";
    }

    @GetMapping("/details/{id}")
    public String detailsBook(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        Optional<Book> book = bookService.findById(id);
        if (book.isEmpty()) {
            model.addAttribute("error", "Book not found");
            return "error-page";
        }

        List<Review> reviews = this.reviewService.findAllByBookId(id);
        Optional<UserReadBook> userReadBookOptional = userReadBookService.findByUsernameAndBookId(username, id);
        Float averageRating = this.reviewService.averageRatingByBookId(id);
        List<Quote> quotes = this.quoteService.findAllByUsernameAndBookId(username, id);

        model.addAttribute("toReadBook", toReadBookService.findByUsernameAndBookId(username, id).isPresent());
        model.addAttribute("book", book.get());
        model.addAttribute("reviews", reviews);
        model.addAttribute("hasRead", userReadBookOptional.isPresent());
        model.addAttribute("userReadBook", userReadBookOptional.orElse(null));
        model.addAttribute("ratings", List.of(1,1.5,2,2.5,3,3.5,4,4.5,5));
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("quotes", quotes);
        return "book-details";
    }

    @GetMapping("/read/{id}")
    public String readBook(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.userReadBookService.add(LocalDate.now(), username, id);
        } catch (IllegalArgumentException | UsernameNotFoundException | BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }
        return "redirect:/books/details/" + id;
    }

    @PostMapping("/read/{id}")
    public String readBookAddDates(@PathVariable Long id, @RequestParam(required = false) LocalDate startedDate,
                                   @RequestParam(required = false) LocalDate finishedDate, Model model,
                                   @AuthenticationPrincipal UserDetails user) {
        if (!bookService.existsById(id)) {
            model.addAttribute("error", "Book not found");
            return "error-page";
        }
        String username = user.getUsername();

        try {
            this.userReadBookService.updateDates(username, id, startedDate, finishedDate);
        } catch (IllegalArgumentException | UserReadBookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }
        return "redirect:/books/details/" + id;

    }

    @GetMapping("/remove-read/{id}")
    public String removeReadBook(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.userReadBookService.deleteByUsernameAndBookId(username, id);
        } catch (UserReadBookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }
        return "redirect:/books/details/" + id;
    }

    @PostMapping("/review/{id}")
    public String addReview(@PathVariable Long id, @RequestParam(required = true) String review,
                             Model model, @AuthenticationPrincipal UserDetails user, @RequestParam(required = false) Float rating) {
        String username = user.getUsername();

        try {
            this.reviewService.addReview(review, username, id, rating);
        } catch (UsernameNotFoundException | BookNotFoundException | IllegalArgumentException |
                 UserReadBookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:/books/details/" + id;
    }

    @PostMapping("/quote/{id}")
    public String addQuote(@PathVariable Long id, @RequestParam(required = true) String quote,
                             Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();

        try {
            this.quoteService.add(username, id, quote);
        } catch (UsernameNotFoundException | BookNotFoundException | IllegalArgumentException |
                 UserReadBookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:/books/details/" + id;
    }

    @GetMapping("/read")
    public String listReadBooks(Model model, @RequestParam(required = false) String search,
                                @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        List<UserReadBook> readBooks;
        if (search == null || search.isBlank()) {
            readBooks = this.userReadBookService.findAllByUsername(username);
        } else {
            readBooks = this.userReadBookService.findAllByUserAndBookTitleContainingIgnoreCase(username, search);
        }
        model.addAttribute("readBooks", readBooks);
        model.addAttribute("search", search);
        return "read-books";
    }

    @GetMapping("/to-read/{id}")
    public String toggleToReadBook(@PathVariable Long id, @RequestParam(required = true) String redirect,
                                   Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.toReadBookService.toggleToRead(username, id);
        } catch (UsernameNotFoundException | BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:" + redirect;
    }

    @GetMapping("/to-read")
    public String getToReadBooks(Model model, @RequestParam(required = false) String search,
                                 @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        List<ToReadBook> toReadBooks;
        if (search == null || search.isBlank()) {
            toReadBooks = this.toReadBookService.findAllByUsername(username);
        } else {
            toReadBooks = this.toReadBookService.findAllByUserAndBookTitleContainingIgnoreCase(username, search);
        }

        model.addAttribute("toReadBooks", toReadBooks);
        return "to-read-books";
    }

    @GetMapping("/currently-reading")
    public String getCurrentlyReadingBooksPage(Model model, @AuthenticationPrincipal UserDetails user,
                                               @RequestParam(required = false) String search) {
        String username = user.getUsername();
        List<CurrentlyReadingBook> currentlyReadingBooks;
        if (search == null || search.isBlank()) {
            currentlyReadingBooks = this.currentlyReadingBookService.findAllByUsername(username);
        } else {
            currentlyReadingBooks = this.currentlyReadingBookService.findAllByUsernameAndBookTitleContainingIgnoreCase(username, search);
        }
        model.addAttribute("currentlyReadingBooks", currentlyReadingBooks);
        return "currently-reading-books";
    }

//    TODO: make a form instead
    @GetMapping("/currently-reading/{id}")
    public String toggleCurrentlyReadingBook(@PathVariable Long id, @RequestParam(required = true) String redirect,
                                   Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.currentlyReadingBookService.toggleCurrentlyReading(username, id);
        } catch (UsernameNotFoundException | BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:" + redirect;
    }
}
