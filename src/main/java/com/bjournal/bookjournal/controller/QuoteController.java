package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.service.QuoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/quotes")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/add/{id}")
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
}
