package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.ToReadBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.service.ToReadBookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/to-read-books")
public class ToReadBookController {
    private final ToReadBookService toReadBookService;

    public ToReadBookController(ToReadBookService toReadBookService) {
        this.toReadBookService = toReadBookService;
    }

    @GetMapping
    public String getAll(Model model, @RequestParam(required = false) String search,
                                 @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        List<ToReadBook> toReadBooks;
        if (search == null || search.isBlank()) {
            toReadBooks = this.toReadBookService.findAllByUsername(username);
        } else {
            toReadBooks = this.toReadBookService.findAllByUserAndBookTitleContainingIgnoreCase(username, search);
        }

        model.addAttribute("toReadBooks", toReadBooks);
        return "/book-lists/to-read-books";
    }

    @GetMapping("/add/{id}")
    public String addToReadBook(@PathVariable Long id, @RequestParam(required = true) String redirect,
                                   Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.toReadBookService.add(username, id);
        } catch (UsernameNotFoundException | BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:" + redirect;
    }

    @GetMapping("/remove/{id}")
    public String removeToReadBook(@PathVariable Long id, @RequestParam(required = true) String redirect,
                                Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.toReadBookService.delete(username, id);
        } catch (UsernameNotFoundException | BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:" + redirect;
    }
}
