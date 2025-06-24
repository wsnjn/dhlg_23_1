package com.example.baiheapp.controller;

import com.example.baiheapp.service.Ad8Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class Ad8Controller {

    @Autowired
    private Ad8Service ad8Service;

    @GetMapping("/questions")
    public Map<String, Object> getQuestions() {
        return ad8Service.getQuestions();
    }

    @PostMapping("/responses")
    public Map<String, Object> submitResponses(@RequestBody Map<String, Object> request) {
        return ad8Service.submitResponses(request);
    }
}