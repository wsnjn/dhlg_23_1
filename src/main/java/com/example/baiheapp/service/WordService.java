package com.example.baiheapp.service;

import com.example.baiheapp.entity.UserWords;
import com.example.baiheapp.repository.UserWordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WordService {

    @Autowired
    private UserWordsRepository userWordsRepository;

    public Map<String, Object> storeWords(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            List<String> words = (List<String>) request.get("words");
            String hospitalNumber = (String) request.get("hospitalNumber");
            String name = (String) request.get("name");

            if (userId == null || words == null || words.isEmpty() || hospitalNumber == null || name == null) {
                response.put("success", false);
                response.put("message", "userId 和非空 words 数组是必填项");
                return response;
            }

            UserWords userWords = new UserWords();
            userWords.setUserId(hospitalNumber + "_" + name);
            userWords.setWordList(String.join(",", words));

            userWordsRepository.save(userWords);

            response.put("success", true);
            response.put("message", "单词存储成功");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误");
            return response;
        }
    }

    public Map<String, Object> getWords(String hospitalNumber, String name) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (hospitalNumber == null || name == null) {
                response.put("success", false);
                response.put("message", "hospitalNumber和name是必填参数");
                return response;
            }

            String userId = hospitalNumber + "_" + name;
            List<UserWords> wordResults = userWordsRepository.findByUserIdOrderByCreatedAtDesc(userId);

            if (!wordResults.isEmpty()) {
                String[] words = wordResults.get(0).getWordList().split(",");
                response.put("success", true);
                response.put("words", words);
            } else {
                response.put("success", true);
                response.put("words", new String[]{"蔚蓝", "胖瘦", "牛奶"});
            }

            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误");
            return response;
        }
    }
}