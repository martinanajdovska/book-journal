package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.service.ReviewService;
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
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add/{id}")
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
}
