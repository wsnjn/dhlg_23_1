package com.example.baiheapp.service;

import com.example.baiheapp.entity.Ad8Question;
import com.example.baiheapp.entity.Ad8Response;
import com.example.baiheapp.repository.Ad8QuestionRepository;
import com.example.baiheapp.repository.Ad8ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
            System.out.println("开始获取 AD8 问题...");

            List<Ad8Question> questions = questionRepository.findAllByOrderByIdAsc();
            System.out.println("获取问题数量: " + questions.size());

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

            System.out.println("问题格式化完成");

            response.put("success", true);
            response.put("data", formattedQuestions);
            response.put("message", "问题获取成功");
            return response;
        } catch (Exception e) {
            System.out.println("获取 AD8 问题时出错:");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取问题失败，请稍后重试");
            return response;
        }
    }

    public Map<String, Object> submitResponses(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("收到 AD8 提交请求: " + request);

            // 处理创建时间
            Object createdAtObj = request.get("created_at");
            Timestamp createdAt = null;

            if (createdAtObj != null) {
                // 尝试解析前端提供的时间
                if (createdAtObj instanceof String) {
                    try {
                        // 假设前端传递的是 "2025-06-23 15:17:35" 格式
                        createdAt = Timestamp.valueOf((String) createdAtObj);
                    } catch (Exception e) {
                        System.out.println("无法解析前端时间: " + createdAtObj);
                    }
                } else if (createdAtObj instanceof Long) {
                    // 如果前端传递的是时间戳
                    createdAt = new Timestamp((Long) createdAtObj);
                }
            }

            // 如果没有有效的时间，使用当前时间
            if (createdAt == null) {
                createdAt = new Timestamp(System.currentTimeMillis());
                System.out.println("使用当前时间作为创建时间: " + createdAt);
            } else {
                System.out.println("使用前端提供的创建时间: " + createdAt);
            }

            Map<String, Object> hospitalInfo = (Map<String, Object>) request.get("hospitalInfo");
            List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");

            if (hospitalInfo == null || hospitalInfo.get("hospitalId") == null ||
                    hospitalInfo.get("patientName") == null || answers == null || answers.size() != 8) {
                System.out.println("参数校验失败，hospitalInfo 或 answers 数组无效");
                response.put("success", false);
                response.put("message", "医院信息和8个问题的answers数组是必填项");
                return response;
            }

            String responseId = "resp_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
            System.out.println("生成 responseId: " + responseId);

            for (Map<String, Object> answer : answers) {
                Ad8Response ad8Response = new Ad8Response();
                ad8Response.setHospitalId(hospitalInfo.get("hospitalId").toString());
                ad8Response.setPatientName(hospitalInfo.get("patientName").toString());
                ad8Response.setResponseId(responseId);
                ad8Response.setQuestionId((Integer) answer.get("question_id"));
                ad8Response.setSelectedOption((Integer) answer.get("selected_option"));

                // 设置创建时间
                ad8Response.setCreatedAt(createdAt);

                System.out.println("保存回答: " + ad8Response);
                responseRepository.save(ad8Response);
            }

            System.out.println("AD8 测试结果保存成功");

            response.put("success", true);
            response.put("message", "测试结果保存成功");
            response.put("response_id", responseId);
            response.put("created_at", createdAt.toString()); // 返回给前端确认
            return response;
        } catch (Exception e) {
            System.out.println("保存 AD8 回答时出错:");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "保存回答错误: " + e.getMessage());
            return response;
        }
    }
}
