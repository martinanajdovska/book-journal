package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.Quote;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface QuoteService {
    void add(String username, Long bookId, String text, MultipartFile image) throws IOException, TesseractException;
    void deleteById(Long id);
    Quote update(Long id, String text);
    List<Quote> findAllByUsernameAndBookId(String username, Long bookId);
    Optional<Quote> findById(Long id);
}
