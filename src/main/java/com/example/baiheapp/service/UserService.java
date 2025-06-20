package com.example.baiheapp.service;

import com.example.baiheapp.entity.User;
import com.example.baiheapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> login(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String name = (String) request.get("name");
            String gender = (String) request.get("gender");
            Integer age = (Integer) request.get("age");
            String hospitalNumber = (String) request.get("hospitalNumber");
            String assessmentDate = (String) request.get("assessmentDate");

            if (name == null || gender == null || age == null || assessmentDate == null) {
                response.put("success", false);
                response.put("message", "姓名、性别、年龄和评定日期为必填项");
                return response;
            }

            List<User> existingUsers = userRepository.findByNameAndGenderAndAgeAndHospitalNumber(
                    name, gender, age, hospitalNumber);

            if (!existingUsers.isEmpty()) {
                response.put("success", true);
                response.put("message", "用户已存在");
                response.put("userId", existingUsers.get(0).getId());
                return response;
            }

            User newUser = new User();
            newUser.setName(name);
            newUser.setGender(gender);
            newUser.setAge(age);
            newUser.setHospitalNumber(hospitalNumber);
            newUser.setAssessmentDate(new Date()); // 简化处理，实际应解析assessmentDate

            User savedUser = userRepository.save(newUser);

            response.put("success", true);
            response.put("message", "用户信息保存成功");
            response.put("userId", savedUser.getId());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误");
            return response;
        }
    }
}