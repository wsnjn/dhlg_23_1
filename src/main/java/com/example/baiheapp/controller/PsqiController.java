package com.example.baiheapp.controller;

import com.example.baiheapp.service.PsqiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class PsqiController {

    @Autowired
    private PsqiService psqiService;

    @GetMapping("/psqi/questions")
    public Map<String, Object> getQuestions() {
        return psqiService.getQuestions();
    }

    @PostMapping("/psqi/responses")
    public Map<String, Object> submitResponses(@RequestBody Map<String, Object> request) {
        return psqiService.submitResponses(request);
    }
}