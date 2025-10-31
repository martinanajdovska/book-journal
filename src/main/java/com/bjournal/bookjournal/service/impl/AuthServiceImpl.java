package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.exceptions.InvalidArgumentsException;
import com.bjournal.bookjournal.model.exceptions.InvalidUserCredentialsException;
import com.bjournal.bookjournal.repository.UserRepository;
import com.bjournal.bookjournal.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }
        return userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(InvalidUserCredentialsException::new);
    }
}
