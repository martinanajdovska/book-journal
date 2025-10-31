package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.User;
import com.bjournal.bookjournal.model.enumerations.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    User register(String username, String password, String repeatPassword, String name, String surname, Role role);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
}

