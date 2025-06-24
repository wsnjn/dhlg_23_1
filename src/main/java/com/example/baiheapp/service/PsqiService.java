package com.example.baiheapp.service;

import com.example.baiheapp.entity.PsqiQuestion;
import com.example.baiheapp.entity.PsqiResponse;
import com.example.baiheapp.repository.PsqiQuestionRepository;
import com.example.baiheapp.repository.PsqiResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PsqiService {

    @Autowired
    private PsqiQuestionRepository questionRepository;

    @Autowired
    private PsqiResponseRepository responseRepository;

    public Map<String, Object> getQuestions() {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("开始获取 PSQI 问题...");

            List<PsqiQuestion> mainQuestions = questionRepository.findByParentIdIsNullOrderByIdAsc();
            System.out.println("主问题数: " + mainQuestions.size());

            List<Map<String, Object>> formattedQuestions = mainQuestions.stream()
                    .map(mainQ -> {
                        Map<String, Object> mainQuestionMap = new HashMap<>();
                        mainQuestionMap.put("id", mainQ.getId());
                        mainQuestionMap.put("text", mainQ.getText());
                        mainQuestionMap.put("options", mainQ.getOptions() != null ?
                                Arrays.asList(mainQ.getOptions().split(";")) : Collections.emptyList());
                        mainQuestionMap.put("type", mainQ.getType());

                        List<PsqiQuestion> subQuestions = questionRepository.findByParentIdOrderByIdAsc(mainQ.getId());
                        System.out.println("主问题 ID: " + mainQ.getId() + " 子问题数: " + subQuestions.size());

                        List<Map<String, Object>> formattedSubQuestions = subQuestions.stream()
                                .map(subQ -> {
                                    Map<String, Object> subQuestionMap = new HashMap<>();
                                    subQuestionMap.put("id", subQ.getId());
                                    subQuestionMap.put("text", subQ.getText());
                                    subQuestionMap.put("options", subQ.getOptions() != null ?
                                            Arrays.asList(subQ.getOptions().split(";")) : Collections.emptyList());
                                    subQuestionMap.put("type", subQ.getType());
                                    return subQuestionMap;
                                })
                                .collect(Collectors.toList());

                        mainQuestionMap.put("subQuestions", formattedSubQuestions);
                        return mainQuestionMap;
                    })
                    .collect(Collectors.toList());

            System.out.println("PSQI 问题获取成功");

            response.put("success", true);
            response.put("data", formattedQuestions);
            response.put("message", "PSQI问题获取成功");
            return response;
        } catch (Exception e) {
            System.out.println("获取 PSQI 问题时出错: ");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取PSQI问题失败，请稍后重试");
            return response;
        }
    }

    public Map<String, Object> submitResponses(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("收到 PSQI 提交请求: " + request);

            Map<String, Object> hospitalInfo = (Map<String, Object>) request.get("hospitalInfo");
            List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");

            // 获取创建时间（如果前端提供）
            Object createdAtObj = request.get("created_at");
            Timestamp createdAt = null;

            if (createdAtObj != null) {
                // 尝试解析前端提供的时间
                if (createdAtObj instanceof String) {
                    try {
                        // 假设前端传递的是 ISO 8601 格式字符串
                        createdAt = Timestamp.valueOf(
                                LocalDateTime.parse(((String) createdAtObj).replace("Z", ""))
                        );
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

            if (hospitalInfo == null || hospitalInfo.get("hospitalId") == null ||
                    hospitalInfo.get("patientName") == null || answers == null) {
                System.out.println("参数校验失败: hospitalInfo 或 answers 缺失");
                response.put("success", false);
                response.put("message", "医院信息和answers数组是必填项");
                return response;
            }

            String responseId = "psqi_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
            System.out.println("生成 responseId: " + responseId);

            for (Map<String, Object> answer : answers) {
                PsqiResponse psqiResponse = new PsqiResponse();
                psqiResponse.setHospitalId(hospitalInfo.get("hospitalId").toString());
                psqiResponse.setPatientName(hospitalInfo.get("patientName").toString());
                psqiResponse.setResponseId(responseId);
                psqiResponse.setQuestionId((Integer) answer.get("question_id"));
                psqiResponse.setAnswerValue(answer.get("answer_value").toString());

                // 设置创建时间
                psqiResponse.setCreatedAt(createdAt);

                System.out.println("保存答案: " + psqiResponse);
                responseRepository.save(psqiResponse);
            }

            System.out.println("PSQI 测试结果保存成功");

            response.put("success", true);
            response.put("message", "PSQI测试结果保存成功");
            response.put("response_id", responseId);
            response.put("created_at", createdAt.toString()); // 返回给前端确认
            return response;
        } catch (Exception e) {
            System.out.println("保存 PSQI 回答时出错: ");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "保存PSQI回答错误: " + e.getMessage());
            return response;
        }
    }
}
