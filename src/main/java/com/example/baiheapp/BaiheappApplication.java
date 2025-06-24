package com.example.baiheapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BaiheappApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaiheappApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "百合APP后端服务已启动！<br>" +
                "可用接口：<br>" +
                "POST /api/login<br>" +
                "POST /api/store-words<br>" +
                "GET /api/select<br>" +
                "GET /questions<br>" +
                "POST /responses<br>" +
                "GET /psqi/questions<br>" +
                "POST /psqi/responses<br>" +
                "GET /user/ad8_responses<br>" +
                "GET /user/psqi_responses<br>" +
                "POST /api/deepseek-chat [支持文件上传]";
    }
}