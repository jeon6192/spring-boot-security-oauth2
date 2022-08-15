package com.example.springbootoauth2.service;

import com.example.springbootoauth2.model.entity.User;
import com.example.springbootoauth2.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Can Not Found User"));
    }
}
