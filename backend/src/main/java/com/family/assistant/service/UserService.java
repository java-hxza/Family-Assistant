package com.family.assistant.service;

import com.family.assistant.entity.User;

public interface UserService {
    User getByUsername(String username);
    User register(User user);
    String login(String username, String password);
} 