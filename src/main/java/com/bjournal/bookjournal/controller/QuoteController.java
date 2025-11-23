package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.Quote;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.QuoteNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.service.QuoteService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/quotes")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/add/{id}")
    public String addQuote(@PathVariable Long id, @RequestParam(required = false) String quote,
                               Model model, @AuthenticationPrincipal UserDetails user,
                               @RequestParam(required = false) MultipartFile file) {
        String username = user.getUsername();
        String contentType = file.getContentType();
        if (!file.isEmpty() && !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            model.addAttribute("error", "Only JPEG or PNG images are allowed");
            model.addAttribute("hasError", true);
            return "/book/book-details";
        }

        try {
            this.quoteService.add(username, id, quote, file);
        } catch (UsernameNotFoundException | BookNotFoundException | IllegalArgumentException |
                 UserReadBookNotFoundException | IOException | TesseractException e) {
            model.addAttribute("error", e.getMessage());
            return "error-page";
        }

        return "redirect:/books/details/" + id;
    }

    @GetMapping("/remove/{id}")
    public String deleteQuote(@PathVariable Long id, Model model, @RequestParam(required = true) String redirect) {
        try {
            this.quoteService.deleteById(id);
        } catch (QuoteNotFoundException e) {
            model.addAttribute("error", "Quote not found");
            return "error-page";
        }
        return "redirect:" + redirect;
    }

    @GetMapping("/edit/{id}")
    public String getEditQuotePage(@PathVariable Long id, Model model) {
        Optional<Quote> quote = quoteService.findById(id);
        if (!quote.isPresent()) {
            model.addAttribute("error", "Quote not found");
            return "error-page";
        }
        model.addAttribute("quote", quote.get());
        return "/quote/edit-form";
    }
//        TODO update quote and redirect to book details
//    @PostMapping("/edit/{id}")
//    public String editQuote(@PathVariable Long id, Model model){
//
//    }
}
