package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(Long id);
    List<Book> findAll();
    List<Book> findAllByAuthor(String author);
    List<Book> findAllByTitle(String title);
    void add(String title, String author, String description, MultipartFile file, Integer pages);
    Optional<Book> update(Long id, String title, String author,  String description, MultipartFile file, Integer pages);
    void deleteById(Long id);
}
