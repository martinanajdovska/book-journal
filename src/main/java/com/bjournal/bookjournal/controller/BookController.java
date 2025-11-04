package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Review;
import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.ReviewService;
import com.bjournal.bookjournal.service.ToReadBookService;
import com.bjournal.bookjournal.service.UserReadBookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserReadBookService userReadBookService;
    private final ToReadBookService toReadBookService;

    public BookController(BookService bookService, ReviewService reviewService, UserReadBookService userReadBookService, ToReadBookService toReadBookService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.userReadBookService = userReadBookService;
        this.toReadBookService = toReadBookService;
    }

    @GetMapping("/add")
    public String getAddBookPage() {
        return "book-form";
    }

    @PostMapping("/add")
    public String addBook(@RequestParam(required = true) String title, @RequestParam(required = true) String author,
                          @RequestParam(required = true) String description, @RequestParam(required = true) MultipartFile file,
                          @RequestParam(required = true) Integer pages, Model model) {
        String contentType = file.getContentType();
        if (contentType==null || !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            model.addAttribute("error", "Only JPEG or PNG images are allowed");
            model.addAttribute("hasError", true);
            return "book-form";
        }
        try {
            this.bookService.add(title, author, description, file, pages);
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
                           @RequestParam(required = true) MultipartFile file, @RequestParam(required = true) Integer pages,
                           Model model) {
        String contentType = file.getContentType();
        if (contentType==null || !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            model.addAttribute("error", "Only JPEG or PNG images are allowed");
            model.addAttribute("hasError", true);
            return "book-form";
        }
        try {
            this.bookService.update(id, title, author, description, file, pages);
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
    public String deleteBook(@PathVariable Long id) {
        this.bookService.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/details/{id}")
    public String detailsBook(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            List<Review> reviews = this.reviewService.findAllByBook(book);
            Optional<UserReadBook> userReadBookOptional;
            try {
                if (toReadBookService.findByUserAndBook(username, book).isPresent()) {
                    model.addAttribute("toReadBook", true);
                } else {
                    model.addAttribute("toReadBook", false);
                }

                userReadBookOptional = userReadBookService.findByUserAndBook(username, book);
            } catch (UsernameNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }
            model.addAttribute("book", book);
            model.addAttribute("reviews", reviews);

            if (userReadBookOptional.isPresent()) {
                UserReadBook hasReadBook = userReadBookOptional.get();
                model.addAttribute("hasRead", true);
                model.addAttribute("userReadBook", hasReadBook);
            } else {
                model.addAttribute("hasRead", false);
                model.addAttribute("userReadBook", null);
            }
            return "book-details";
        }
        model.addAttribute("error", "Book not found");
        return "error-page";
    }

    @GetMapping("/read/{id}")
    public String readBook(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            String username = user.getUsername();

            try {
                this.userReadBookService.add(LocalDate.now(), username, book);
            } catch (IllegalArgumentException | UsernameNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }
            return "redirect:/books/details/" + id;
        }
        model.addAttribute("error", "Book not found");
        return "error-page";
    }

    @PostMapping("/read/{id}")
    public String readBookAddDates(@PathVariable Long id, @RequestParam(required = false) LocalDate startedDate,
                                   @RequestParam(required = false) LocalDate finishedDate, Model model,
                                   @AuthenticationPrincipal UserDetails user) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            String username = user.getUsername();

            Optional<UserReadBook> userReadBookOptional;
            try {
                userReadBookOptional = userReadBookService.findByUserAndBook(username, book);

            } catch (UsernameNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }
            if (userReadBookOptional.isPresent()) {
                UserReadBook hasReadBook = userReadBookOptional.get();
                try {
                    this.userReadBookService.updateDates(hasReadBook, startedDate, finishedDate);
                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", e.getMessage());
                    return "error-page";
                }
                return "redirect:/books/details/" + id;
            } else {
                model.addAttribute("error", "User has not read this book");
                return "error-page";
            }
        }
        model.addAttribute("error", "Book not found");
        return "error-page";
    }

    @GetMapping("/remove-read/{id}")
    public String removeReadBook(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            String username = user.getUsername();

            try {
                this.userReadBookService.deleteByUserAndBook(username, book);
            } catch (UsernameNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }

            return "redirect:/books/details/" + id;
        }
        model.addAttribute("error", "Book not found");
        return "error-page";
    }

    @PostMapping("/review/{id}")
    public String reviewBook(@PathVariable Long id, @RequestParam(required = true) String review,
                             Model model, @AuthenticationPrincipal UserDetails user) {
        if (this.bookService.findById(id).isPresent()) {
            String username = user.getUsername();

            try {
                this.reviewService.addReview(review, username, id);
            } catch (UsernameNotFoundException | BookNotFoundException | IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }

            return "redirect:/books/details/" + id;
        }
        model.addAttribute("error", "Book not found");
        return "error-page";
    }

    @GetMapping("/read")
    public String listReadBooks(Model model, @RequestParam(required = false) String search,
                                @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        List<UserReadBook> readBooks;
        if (search == null || search.isBlank()) {
            try {
                readBooks = this.userReadBookService.findAllByUser(username);
            } catch (UsernameNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }
        } else {
            try {
                readBooks = this.userReadBookService.findAllByUserAndBookTitleContainingIgnoreCase(username, search);
            } catch (UsernameNotFoundException | IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }
        }
        model.addAttribute("readBooks", readBooks);
        model.addAttribute("search", search);
        return "read-books";
    }

    @GetMapping("/to-read/{id}")
    public String toggleToReadBook(@PathVariable Long id, @RequestParam(required = true) String redirect,
                                   Model model, @AuthenticationPrincipal UserDetails user) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            String username = user.getUsername();

            try {
                this.toReadBookService.toggleToRead(username, book);
            } catch (UsernameNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                return "error-page";
            }

            return "redirect:" + redirect;
        }
        model.addAttribute("error", "Book not found");
        return "error-page";
    }
}
