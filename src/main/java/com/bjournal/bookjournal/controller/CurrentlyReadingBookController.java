package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.CurrentlyReadingBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.service.CurrentlyReadingBookService;
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
@RequestMapping("/currently-reading-books")
public class CurrentlyReadingBookController {
    private final CurrentlyReadingBookService  currentlyReadingBookService;

    public CurrentlyReadingBookController(CurrentlyReadingBookService currentlyReadingBookService) {
        this.currentlyReadingBookService = currentlyReadingBookService;
    }

    @GetMapping
    public String getAll(Model model, @AuthenticationPrincipal UserDetails user,
                                               @RequestParam(required = false) String search) {
        String username = user.getUsername();
        List<CurrentlyReadingBook> currentlyReadingBooks;
        if (search == null || search.isBlank()) {
            currentlyReadingBooks = this.currentlyReadingBookService.findAllByUsername(username);
        } else {
            currentlyReadingBooks = this.currentlyReadingBookService.findAllByUsernameAndBookTitleContainingIgnoreCase(username, search);
        }
        model.addAttribute("currentlyReadingBooks", currentlyReadingBooks);
        return "/book-lists/currently-reading-books";
    }

    //    TODO: make a form instead
    @GetMapping("/add/{id}")
    public String addCurrentlyReadingBook(@PathVariable Long id, @RequestParam(required = true) String redirect,
                                             Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.currentlyReadingBookService.add(username, id);
        } catch (UsernameNotFoundException | BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:" + redirect;
    }

    @GetMapping("/remove/{id}")
    public String deleteCurrentlyReadingBook(@PathVariable Long id, @RequestParam(required = true) String redirect,
                                             Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        try {
            this.currentlyReadingBookService.delete(username, id);
        } catch (UsernameNotFoundException | BookNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:" + redirect;
    }
}
