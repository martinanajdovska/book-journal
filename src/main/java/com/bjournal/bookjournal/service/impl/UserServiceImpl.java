package com.bjournal.bookjournal.service.impl;

import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.enumerations.Role;
import com.bjournal.bookjournal.model.exceptions.InvalidArgumentsException;
import com.bjournal.bookjournal.model.exceptions.PasswordsDoNotMatchException;
import com.bjournal.bookjournal.model.exceptions.UsernameAlreadyExistsException;
import com.bjournal.bookjournal.repository.UserRepository;
import com.bjournal.bookjournal.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String username, String password, String repeatPassword, String name, String surname, Role role) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if (this.userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        User user = new User(username, passwordEncoder.encode(password), name, surname, role);

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        return this.userRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}

