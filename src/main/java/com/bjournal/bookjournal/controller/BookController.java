package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.*;
import com.bjournal.bookjournal.model.enumerations.Genre;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({"/books", "/"})
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

    @GetMapping
    public String getAll(Model model, @RequestParam(required = false) String search,
                        @RequestParam(required = false) String error) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        List<Book> books;
        if (search != null && !search.isBlank()) {
            books = bookService.findAllByTitle(search);
        } else {
            books = bookService.findAll();
        }
        model.addAttribute("books", books);
        model.addAttribute("search", search);
        return "/book-lists/index";
    }

    @GetMapping("/add")
    public String getAddBookPage(Model model) {
        List<Genre> genres = Arrays.stream(Genre.values()).sorted(Comparator.comparing(Enum::name)).toList();
        model.addAttribute("genres", genres);
        return "/book/book-form";
    }

    @PostMapping("/add")
    public String addBook(@RequestParam(required = true) String title, @RequestParam(required = true) String author,
                          @RequestParam(required = true) String description, @RequestParam(required = true) MultipartFile file,
                          @RequestParam(required = true) Integer pages, @RequestParam(required = true) Genre genre, Model model) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            model.addAttribute("error", "Only JPEG or PNG images are allowed");
            model.addAttribute("hasError", true);
            return "/book/book-form";
        }
        try {
            this.bookService.add(title, author, description, file, pages, genre);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("hasError", true);
            return "/book/book-form";
        } catch (IOException e) {
            model.addAttribute("error", "Error while saving the image");
            model.addAttribute("hasError", true);
            return "/book/book-form";
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
            return "/book/book-form";
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
                return "/book/book-form";
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
            return "/book/book-form";
        } catch (IOException e) {
            Book book = bookService.findById(id).get();
            model.addAttribute("book", book);
            model.addAttribute("error", "Error while saving the image");
            model.addAttribute("hasError", true);
            return "/book/book-form";
        }
        return "redirect:/";
    }

    @GetMapping("/remove/{id}")
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

        model.addAttribute("toReadBook", toReadBookService.findByUsernameAndBookId(username, id).isPresent());
        model.addAttribute("book", book.get());
        model.addAttribute("reviews", reviewService.findAllByBookId(id));
        model.addAttribute("userReadBook", userReadBookService.findLastByUserUsernameAndBookId(username, id).orElse(null));
        model.addAttribute("isCurrentlyReadingBook", currentlyReadingBookService.findByUsernameAndBookId(username,id).isPresent());
        model.addAttribute("ratings", List.of(1,1.5,2,2.5,3,3.5,4,4.5,5));
        model.addAttribute("averageRating", reviewService.averageRatingByBookId(id));
        model.addAttribute("quotes", quoteService.findAllByUsernameAndBookId(username, id));
        model.addAttribute("hasError", false);

        return "/book/book-details";
    }

    @PostMapping("/state/{id}")
    public String stateBook(@PathVariable Long id, @RequestParam(required = false) String state, Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        Optional<Book> book = bookService.findById(id);
        if (book.isEmpty()) {
            model.addAttribute("error", "Book not found");
            return "error-page";
        }

        switch (state) {
            case "toRead":
                currentlyReadingBookService.delete(username, id);
                return "redirect:/to-read-books/add/"+id+"?redirect=/books/details/"+id;
            case "hasRead":
                toReadBookService.delete(username, id);
                currentlyReadingBookService.delete(username, id);
                return "redirect:/read-books/add/"+id;
            case "isCurrentlyReading":
                toReadBookService.delete(username, id);
                return "redirect:/currently-reading-books/add/"+id+"?redirect=/books/details/"+id;
            case "remove":
                toReadBookService.delete(username, id);
                currentlyReadingBookService.delete(username, id);
                userReadBookService.delete(username, id);
        }
        model.addAttribute("userReadBook", null);
        model.addAttribute("isCurrentlyReadingBook", false);
        model.addAttribute("toReadBook", false);
        model.addAttribute("book", book.get());
        model.addAttribute("ratings", List.of(1,1.5,2,2.5,3,3.5,4,4.5,5));
        model.addAttribute("quotes", quoteService.findAllByUsernameAndBookId(username, id));
        model.addAttribute("averageRating", reviewService.averageRatingByBookId(id));
        model.addAttribute("reviews", reviewService.findAllByBookId(id));
        model.addAttribute("hasError", false);

        return "/book/book-details";
    }
}
