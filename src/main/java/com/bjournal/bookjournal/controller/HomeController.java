package com.bjournal.bookjournal.controller;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {
    private final BookService bookService;

    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping()
    public String index(Model model, @RequestParam(required = false) String search,
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
        return "index";
    }
}
