package com.example.baiheapp.controller;

import com.example.baiheapp.service.UserService;
import com.example.baiheapp.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private WordService wordService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> request) {
        return userService.login(request);
    }

    @PostMapping("/store-words")
    public Map<String, Object> storeWords(@RequestBody Map<String, Object> request) {
        return wordService.storeWords(request);
    }

    @GetMapping("/select")
    public Map<String, Object> getWords(
            @RequestParam String hospitalNumber,
            @RequestParam String name) {
        return wordService.getWords(hospitalNumber, name);
    }
}