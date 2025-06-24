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
            System.out.println("收到登录请求: " + request);

            String name = (String) request.get("name");
            String gender = (String) request.get("gender");
            Integer age = (Integer) request.get("age");
            String hospitalNumber = (String) request.get("hospitalNumber");
            String assessmentDate = (String) request.get("assessmentDate");

            System.out.println("解析参数 - name: " + name + ", gender: " + gender + ", age: " + age
                    + ", hospitalNumber: " + hospitalNumber + ", assessmentDate: " + assessmentDate);

            if (name == null || gender == null || age == null || assessmentDate == null) {
                System.out.println("参数校验失败: 必填项缺失");
                response.put("success", false);
                response.put("message", "姓名、性别、年龄和评定日期为必填项");
                return response;
            }

            System.out.println("查询用户是否已存在...");
            List<User> existingUsers = userRepository.findByNameAndGenderAndAgeAndHospitalNumber(
                    name, gender, age, hospitalNumber);

            System.out.println("已存在用户数: " + existingUsers.size());

            if (!existingUsers.isEmpty()) {
                System.out.println("用户已存在, userId: " + existingUsers.get(0).getId());
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
            newUser.setAssessmentDate(new Date()); // 简化处理

            System.out.println("准备保存新用户: " + newUser);

            User savedUser = userRepository.save(newUser);

            System.out.println("新用户保存成功, userId: " + savedUser.getId());

            response.put("success", true);
            response.put("message", "用户信息保存成功");
            response.put("userId", savedUser.getId());
            return response;
        } catch (Exception e) {
            System.out.println("登录过程出错: ");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "服务器内部错误");
            return response;
        }
    }
}
