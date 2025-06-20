package com.example.baiheapp.service;

import com.example.baiheapp.entity.Ad8Question;
import com.example.baiheapp.entity.Ad8Response;
import com.example.baiheapp.repository.Ad8QuestionRepository;
import com.example.baiheapp.repository.Ad8ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Ad8Service {

    @Autowired
    private Ad8QuestionRepository questionRepository;

    @Autowired
    private Ad8ResponseRepository responseRepository;

    public Map<String, Object> getQuestions() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Ad8Question> questions = questionRepository.findAllByOrderByIdAsc();

            List<Map<String, Object>> formattedQuestions = questions.stream()
                    .map(q -> {
                        Map<String, Object> questionMap = new HashMap<>();
                        questionMap.put("id", q.getId());
                        questionMap.put("text", q.getText());
                        questionMap.put("options", Arrays.asList(q.getOption1(), q.getOption2(), q.getOption3()));
                        questionMap.put("correct", q.getCorrect());
                        return questionMap;
                    })
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", formattedQuestions);
            response.put("message", "问题获取成功");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取问题失败，请稍后重试");
            return response;
        }
    }

    public Map<String, Object> submitResponses(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> hospitalInfo = (Map<String, Object>) request.get("hospitalInfo");
            List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");

            if (hospitalInfo == null || hospitalInfo.get("hospitalId") == null ||
                    hospitalInfo.get("patientName") == null || answers == null || answers.size() != 8) {
                response.put("success", false);
                response.put("message", "医院信息和8个问题的answers数组是必填项");
                return response;
            }

            String responseId = "resp_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);

            for (Map<String, Object> answer : answers) {
                Ad8Response ad8Response = new Ad8Response();
                ad8Response.setHospitalId(hospitalInfo.get("hospitalId").toString());
                ad8Response.setPatientName(hospitalInfo.get("patientName").toString());
                ad8Response.setResponseId(responseId);
                ad8Response.setQuestionId((Integer) answer.get("question_id"));
                ad8Response.setSelectedOption((Integer) answer.get("selected_option"));

                responseRepository.save(ad8Response);
            }

            response.put("success", true);
            response.put("message", "测试结果保存成功");
            response.put("response_id", responseId);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存回答错误: " + e.getMessage());
            return response;
        }
    }
}