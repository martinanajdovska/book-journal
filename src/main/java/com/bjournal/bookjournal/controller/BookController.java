package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Review;
import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserNotAuthenticatedException;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.ReviewService;
import com.bjournal.bookjournal.service.ToReadBookService;
import com.bjournal.bookjournal.service.UserReadBookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public String addBook() {
        return "book-form";
    }

    @PostMapping("/add")
    public String addBook(@RequestParam(required = true) String title, @RequestParam(required = true) String author,
                          @RequestParam(required = true) String description, @RequestParam(required = true) MultipartFile file,
                          @RequestParam(required = true) Integer pages, Model model) {
        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
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
    public String editBook(@PathVariable Long id, Model model) {
        if (this.bookService.findById(id).isPresent()) {
            model.addAttribute("book", bookService.findById(id).get());
            return "book-form";
        }
        return "redirect:/?error=BookNotFound";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, @RequestParam(required = true) String title,
                           @RequestParam(required = true) String author, @RequestParam(required = true) String description,
                           @RequestParam(required = true) MultipartFile file, @RequestParam(required = true) Integer pages,
                           Model model) {
        try {
            this.bookService.update(id, title, author, description, file, pages);
        } catch (BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/?error=BookNotFound";
        } catch (IllegalArgumentException e) {
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
    public String detailsBook(@PathVariable Long id, Model model, HttpServletRequest req) {
        String username = req.getRemoteUser();
        if (username == null) {
            throw new UserNotAuthenticatedException();
        }
        if (this.bookService.findById(id).isPresent()) {
            Book book = this.bookService.findById(id).get();
            List<Review> reviews = this.reviewService.findAllByBook(book);
            if (toReadBookService.findByUserAndBook(username, book).isPresent()) {
                model.addAttribute("toReadBook", true);
            } else {
                model.addAttribute("toReadBook", false);
            }
            model.addAttribute("book", book);
            model.addAttribute("reviews", reviews);
            if (userReadBookService.findByUserAndBook(username, book).isPresent()) {
                UserReadBook hasReadBook = userReadBookService.findByUserAndBook(username, book).get();
                model.addAttribute("hasRead", true);
                model.addAttribute("userReadBook", hasReadBook);
            } else {
                model.addAttribute("hasRead", false);
                model.addAttribute("userReadBook", null);
            }
            return "book-details";
        }
        return "redirect:/?error=BookNotFound";
    }

    @GetMapping("/read/{id}")
    public String readBook(@PathVariable Long id, HttpServletRequest req) {
        if (this.bookService.findById(id).isPresent()) {
            Book book = this.bookService.findById(id).get();
            String username = req.getRemoteUser();
            if (username == null) {
                throw new UserNotAuthenticatedException();
            }

            this.userReadBookService.add(LocalDate.now(), username, book);
            return "redirect:/books/details/" + id;
        }
        return "redirect:/?error=BookNotFound";
    }

    @PostMapping("/read/{id}")
    public String readBook(@PathVariable Long id, @RequestParam(required = false) LocalDate startedDate,
                           @RequestParam(required = false) LocalDate finishedDate, HttpServletRequest req) {
        if (this.bookService.findById(id).isPresent()) {
            Book book = this.bookService.findById(id).get();
            String username = req.getRemoteUser();
            if (username == null) {
                throw new UserNotAuthenticatedException();
            }

            if (userReadBookService.findByUserAndBook(username, book).isPresent()) {
                UserReadBook hasReadBook = userReadBookService.findByUserAndBook(username, book).get();
                this.userReadBookService.update(hasReadBook, startedDate, finishedDate);
                return "redirect:/books/details/" + id;
            } else {
                return "redirect:/?error=BookNotFound";
            }
        }
        return "redirect:/?error=BookNotFound";
    }

    @GetMapping("/remove-read/{id}")
    public String removeReadBook(@PathVariable Long id, HttpServletRequest req) {
        if (this.bookService.findById(id).isPresent()) {
            Book book = this.bookService.findById(id).get();
            String username = req.getRemoteUser();
            if (username == null) {
                throw new UserNotAuthenticatedException();
            }

            this.userReadBookService.deleteByUserAndBook(username, book);
            return "redirect:/books/details/" + id;
        }
        return "redirect:/?error=BookNotFound";
    }

    @PostMapping("/review/{id}")
    public String reviewBook(@PathVariable Long id, @RequestParam(required = true) String review, HttpServletRequest req) {
        if (this.bookService.findById(id).isPresent()) {
            String username = req.getRemoteUser();
            if (username == null) {
                throw new UserNotAuthenticatedException();
            }

            this.reviewService.addReview(review, username, id);
            return "redirect:/books/details/" + id;
        }
        return "redirect:/?error=BookNotFound";
    }

    @GetMapping("/read")
    public String listReadBooks(HttpServletRequest req, Model model, @RequestParam(required = false) String search) {
        String username = req.getRemoteUser();
        if (username == null) {
            throw new UserNotAuthenticatedException();
        }
        List<UserReadBook> readBooks = new ArrayList<>();
        if (search == null || search.isBlank()) {
            readBooks = this.userReadBookService.findAllByUser(username);
        } else {
            readBooks = this.userReadBookService.findAllByUserAndBookTitleContainingIgnoreCase(username, search);
        }
        model.addAttribute("readBooks", readBooks);
        model.addAttribute("search", search);
        return "read-books";
    }

    @GetMapping("/to-read/{id}")
    public String toReadBook(@PathVariable Long id, @RequestParam(required = true) String redirect, HttpServletRequest req) {
        if (this.bookService.findById(id).isPresent()) {
            Book book = this.bookService.findById(id).get();
            String username = req.getRemoteUser();
            if (username == null) {
                throw new UserNotAuthenticatedException();
            }

            this.toReadBookService.toggleToRead(username,book);
            return "redirect:"+redirect;
        }
        return "redirect:/?error=BookNotFound";
    }
}
