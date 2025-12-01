package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.BorrowedBook;
import com.bjournal.bookjournal.service.BorrowedBookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/borrowed-books")
public class BorrowedBookController {
    private final BorrowedBookService borrowedBookService;

    public BorrowedBookController(BorrowedBookService borrowedBookService) {
        this.borrowedBookService = borrowedBookService;
    }

    @GetMapping
    public String getAll(Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        List<BorrowedBook> borrowedBookList = this.borrowedBookService.findAllByUser(username);
        model.addAttribute("borrowedBooks", borrowedBookList);
        model.addAttribute("id",null);
        return "/book-lists/borrowed-books";
    }

    @PostMapping("/add")
    public String add(@AuthenticationPrincipal UserDetails user, @RequestParam(required = true) String text) {
        String username = user.getUsername();
        this.borrowedBookService.add(text, username);
        return "redirect:/borrowed-books";
    }

    @GetMapping("/remove/{id}")
    public String delete(@PathVariable Long id) {
        this.borrowedBookService.delete(id);
        return "redirect:/borrowed-books";
    }

    @GetMapping("/edit/{id}")
    public String getEdit(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails user) {
        String username = user.getUsername();
        List<BorrowedBook> borrowedBookList = this.borrowedBookService.findAllByUser(username);
        model.addAttribute("borrowedBooks", borrowedBookList);
        model.addAttribute("id",id);
        return "/book-lists/borrowed-books";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam(required = true) Long id, @RequestParam(required = true) String updated) {
        this.borrowedBookService.update(id, updated);
        return "redirect:/borrowed-books";
    }
}
