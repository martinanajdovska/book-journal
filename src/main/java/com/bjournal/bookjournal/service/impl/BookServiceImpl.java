package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.repository.BookRepository;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<Book> findById(Long id) {
        return this.bookRepository.findById(id);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findAllByAuthor(String author) {
        return bookRepository.findAllByAuthor(author);
    }

    @Override
    public List<Book> findAllByTitle(String title) {
        return bookRepository.findAllByTitleContainingIgnoreCase(title);
    }

    @Override
    public void add(String title, String author, String description, MultipartFile file, Integer pages) throws IOException {
        if (title == null || author == null || description == null
                || file == null || title.isBlank() || author.isBlank()
                || description.isBlank() || file.isEmpty() || pages == null || pages == 0) {
            throw new IllegalArgumentException("Invalid input");
        }
        String filePath = saveImage(file, title, author);
        bookRepository.save(new Book(title, author, description, filePath, pages));
    }

    private String saveImage(MultipartFile file, String title, String author) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalFilename = file.getOriginalFilename();
        String fileName = String.format("%s-%s.%s", title, author, originalFilename.substring(originalFilename.lastIndexOf(".") + 1));
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    @Override
    public Optional<Book> update(Long id, String title, String author, String description,
                                 MultipartFile file, Integer pages) throws IOException {
        Book book = findById(id).orElseThrow(() -> new BookNotFoundException(id));

        if (title == null || author == null || description == null
                || file == null || title.isBlank() || author.isBlank()
                || description.isBlank() || file.isEmpty() || pages == null || pages == 0) {
            throw new IllegalArgumentException("Invalid input");
        }
        String filePath = saveImage(file, title, author);

        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setFile(filePath);
        book.setPages(pages);
        bookRepository.save(book);
        return Optional.of(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
