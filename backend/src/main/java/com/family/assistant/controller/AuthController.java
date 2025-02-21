package com.family.assistant.controller;

import com.family.assistant.common.Result;
import com.family.assistant.entity.User;
import com.family.assistant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        return Result.success(userService.register(user));
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        return Result.success(token);
    }
} 