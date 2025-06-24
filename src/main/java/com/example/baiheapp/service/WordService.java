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
            System.out.println("收到存储请求: " + request);

            String userId = request.get("userId") != null ? request.get("userId").toString() : null;
            List<String> words = (List<String>) request.get("words");
            String hospitalNumber = (String) request.get("hospitalNumber");
            String name = (String) request.get("name");

            System.out.println("解析参数 - userId: " + userId + ", hospitalNumber: " + hospitalNumber + ", name: " + name);
            System.out.println("解析参数 - words: " + words);

            if (userId == null || words == null || words.isEmpty() || hospitalNumber == null || name == null) {
                System.out.println("参数校验失败");
                response.put("success", false);
                response.put("message", "userId 和非空 words 数组是必填项");
                return response;
            }

            UserWords userWords = new UserWords();
            userWords.setUserId(hospitalNumber + "_" + name);
            userWords.setWordList(String.join(",", words));

            System.out.println("准备保存到数据库: " + userWords);

            UserWords saved = userWordsRepository.save(userWords);

            System.out.println("保存成功，记录ID: " + saved.getId());

            response.put("success", true);
            response.put("message", "单词存储成功");
            response.put("recordId", saved.getId());
            return response;
        } catch (Exception e) {
            System.out.println("存储过程出错: ");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "服务器内部错误");
            return response;
        }
    }

    public Map<String, Object> getWords(String hospitalNumber, String name) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("收到查询请求 - hospitalNumber: " + hospitalNumber + ", name: " + name);

            if (hospitalNumber == null || name == null) {
                System.out.println("参数校验失败: hospitalNumber 或 name 为 null");
                response.put("success", false);
                response.put("message", "hospitalNumber和name是必填参数");
                return response;
            }

            String userId = hospitalNumber + "_" + name;
            System.out.println("拼接 userId: " + userId);

            // 修改这里：获取用户的最新记录
            List<UserWords> wordResults = userWordsRepository.findByUserIdOrderByIdDesc(userId);
            // 修改这里：确保取到最新的一条记录
            Optional<UserWords> latestRecord = wordResults.stream().findFirst();

            System.out.println("查询结果条数: " + wordResults.size());

            if (!wordResults.isEmpty()) {
                String[] words = wordResults.get(0).getWordList().split(",");
                System.out.println("返回用户单词: " + Arrays.toString(words));
                response.put("success", true);
                response.put("words", words);
            } else {
                System.out.println("未找到记录，返回默认单词");
                response.put("success", true);
                response.put("words", new String[]{"蔚蓝", "胖瘦", "牛奶"});
            }

            return response;
        } catch (Exception e) {
            System.out.println("查询过程出错: ");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "服务器内部错误");
            return response;
        }
    }
}
