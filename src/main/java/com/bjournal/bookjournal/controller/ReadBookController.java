package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.UserReadBookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/read-books")
public class ReadBookController {
    private final UserReadBookService userReadBookService;
    private final BookService  bookService;

    public ReadBookController(UserReadBookService userReadBookService, BookService bookService) {
        this.userReadBookService = userReadBookService;
        this.bookService = bookService;
    }

    @GetMapping
    public String getAll(Model model, @RequestParam(required = false) String search,
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
        return "/book-lists/read-books";
    }

    @GetMapping("/add/{id}")
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

    @PostMapping("/add/{id}")
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

    @GetMapping("/remove/{id}")
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
}
