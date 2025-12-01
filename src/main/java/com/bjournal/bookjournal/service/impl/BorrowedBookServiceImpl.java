package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.BorrowedBook;
import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.exceptions.BorrowedBookNotFoundException;
import com.bjournal.bookjournal.model.exceptions.InvalidArgumentsException;
import com.bjournal.bookjournal.repository.BorrowedBookRepository;
import com.bjournal.bookjournal.service.BorrowedBookService;
import com.bjournal.bookjournal.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowedBookServiceImpl implements BorrowedBookService {
    private final BorrowedBookRepository borrowedBookRepository;
    private final UserService userService;

    public BorrowedBookServiceImpl(BorrowedBookRepository borrowedBookRepository, UserService userService) {
        this.borrowedBookRepository = borrowedBookRepository;
        this.userService = userService;
    }

    @Override
    public List<BorrowedBook> findAllByUser(String username) {
        return this.borrowedBookRepository.findAllByUserUsername(username);
    }

    @Override
    public void add(String text, String username) {
        if (text.isEmpty() || text == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        User user  = userService.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        BorrowedBook borrowedBook = new BorrowedBook(text,user);
        borrowedBookRepository.save(borrowedBook);
    }

    @Override
    public BorrowedBook update(Long id, String text) {
        if (text.isEmpty() || text == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        BorrowedBook borrowedBook = this.borrowedBookRepository.findById(id).orElseThrow(()-> new BorrowedBookNotFoundException());
        borrowedBook.setText(text);
        return this.borrowedBookRepository.save(borrowedBook);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        this.borrowedBookRepository.deleteById(id);
    }
}
