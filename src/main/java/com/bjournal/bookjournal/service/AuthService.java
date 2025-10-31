package com.bjournal.bookjournal.service;

import com.bjournal.bookjournal.model.User;

public interface AuthService {
    User login(String username, String password);
}
