package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.Book;
import com.bjournal.bookjournal.model.Quote;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.UserReadBook;
import com.bjournal.bookjournal.model.exceptions.BookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.QuoteNotFoundException;
import com.bjournal.bookjournal.model.exceptions.UserReadBookNotFoundException;
import com.bjournal.bookjournal.repository.QuoteRepository;
import com.bjournal.bookjournal.service.BookService;
import com.bjournal.bookjournal.service.QuoteService;
import com.bjournal.bookjournal.service.UserReadBookService;
import com.bjournal.bookjournal.service.UserService;
import jakarta.transaction.Transactional;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserService userService;
    private final BookService bookService;
    private final UserReadBookService userReadBookService;

    public QuoteServiceImpl(QuoteRepository quoteRepository, UserService userService, BookService bookService, UserReadBookService userReadBookService) {
        this.quoteRepository = quoteRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.userReadBookService = userReadBookService;
    }

    @Override
    public void add(String username, Long bookId, String text, MultipartFile file) throws TesseractException, IOException {
        if (username == null || bookId == null || (text.isEmpty() && file.isEmpty())){
            throw new IllegalArgumentException("Invalid arguments");
        }
        User user = this.userService.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
        Book book = this.bookService.findById(bookId).orElseThrow(()->new BookNotFoundException(bookId));
        UserReadBook userReadBook = this.userReadBookService.findLastByUserUsernameAndBookId(username,bookId).orElseThrow(()-> new UserReadBookNotFoundException(username,bookId));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");

        if(text.isEmpty()){
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("src/main/resources/tessdata");
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);

            File tmp = new File(file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(tmp);
            fos.write(file.getBytes());
            fos.close();
            text = tesseract.doOCR(tmp);
            text = text.trim();

            // fix the new lines that ocr adds from the pages
            List<String> words = Arrays.asList(text.split("\n"));
            words.forEach(word -> {stringBuilder.append(word).append(" ");});

            tmp.delete();
        }

        stringBuilder.append("\"");
        this.quoteRepository.save(new Quote(book,user,stringBuilder.toString()));
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        quoteRepository.findById(id).orElseThrow(()->new QuoteNotFoundException(id));
        this.quoteRepository.deleteById(id);
    }

    @Override
    public Quote update(Long id, String text) {
        if (id == null || text == null || text.isBlank()) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        Quote quote = this.quoteRepository.findById(id).orElseThrow(()->new QuoteNotFoundException(id));
        quote.setText(text);
        this.quoteRepository.save(quote);
        return quote;
    }

    @Transactional
    @Override
    public List<Quote> findAllByUsernameAndBookId(String username, Long bookId) {
        return this.quoteRepository.findAllByUserUsernameAndBookId(username, bookId);
    }

    @Override
    public Optional<Quote> findById(Long id) {
        return this.quoteRepository.findById(id);
    }
}
