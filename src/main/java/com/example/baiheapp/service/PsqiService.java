package com.example.baiheapp.service;

import com.example.baiheapp.entity.PsqiQuestion;
import com.example.baiheapp.entity.PsqiResponse;
import com.example.baiheapp.repository.PsqiQuestionRepository;
import com.example.baiheapp.repository.PsqiResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            List<PsqiQuestion> mainQuestions = questionRepository.findByParentIdIsNullOrderByIdAsc();

            List<Map<String, Object>> formattedQuestions = mainQuestions.stream()
                    .map(mainQ -> {
                        Map<String, Object> mainQuestionMap = new HashMap<>();
                        mainQuestionMap.put("id", mainQ.getId());
                        mainQuestionMap.put("text", mainQ.getText());
                        mainQuestionMap.put("options", mainQ.getOptions() != null ?
                                Arrays.asList(mainQ.getOptions().split(";")) : Collections.emptyList());
                        mainQuestionMap.put("type", mainQ.getType());

                        List<PsqiQuestion> subQuestions = questionRepository.findByParentIdOrderByIdAsc(mainQ.getId());
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

            response.put("success", true);
            response.put("data", formattedQuestions);
            response.put("message", "PSQI问题获取成功");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取PSQI问题失败，请稍后重试");
            return response;
        }
    }

    public Map<String, Object> submitResponses(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> hospitalInfo = (Map<String, Object>) request.get("hospitalInfo");
            List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");

            if (hospitalInfo == null || hospitalInfo.get("hospitalId") == null ||
                    hospitalInfo.get("patientName") == null || answers == null) {
                response.put("success", false);
                response.put("message", "医院信息和answers数组是必填项");
                return response;
            }

            String responseId = "psqi_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);

            for (Map<String, Object> answer : answers) {
                PsqiResponse psqiResponse = new PsqiResponse();
                psqiResponse.setHospitalId(hospitalInfo.get("hospitalId").toString());
                psqiResponse.setPatientName(hospitalInfo.get("patientName").toString());
                psqiResponse.setResponseId(responseId);
                psqiResponse.setQuestionId((Integer) answer.get("question_id"));
                psqiResponse.setAnswerValue(answer.get("answer_value").toString());

                responseRepository.save(psqiResponse);
            }

            response.put("success", true);
            response.put("message", "PSQI测试结果保存成功");
            response.put("response_id", responseId);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存PSQI回答错误: " + e.getMessage());
            return response;
        }
    }
}