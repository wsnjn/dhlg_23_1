package com.example.baiheapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class DeepSeekController {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekController.class);
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final List<String> TEXT_MIME_TYPES = Arrays.asList(
            "text/plain", "text/csv", "text/html", "text/css",
            "text/javascript", "application/json", "application/xml"
    );

    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/api/deepseek-chat")
    public ResponseEntity<?> deepSeekChat(
            @RequestPart(value = "messages", required = false) String messagesJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        try {
            logger.info("Received DeepSeek chat request");

            // 检查文件大小限制
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        logger.warn("File exceeds size limit: {} - {} bytes",
                                file.getOriginalFilename(), file.getSize());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error",
                                        "文件过大: " + file.getOriginalFilename() +
                                                " (最大支持2MB)"));
                    }
                }
            }

            List<Map<String, Object>> messages = new ArrayList<>();

            // 解析文本消息
            if (messagesJson != null && !messagesJson.isEmpty()) {
                logger.debug("Parsing messages JSON: {}", messagesJson);
                messages = objectMapper.readValue(messagesJson, new TypeReference<>() {});
                logger.info("Loaded {} message(s) from request", messages.size());
            }

            // 处理上传文件
            if (files != null && files.length > 0) {
                logger.info("Processing {} uploaded file(s)", files.length);
                for (MultipartFile file : files) {
                    String fileName = null;
                    if (!file.isEmpty()) {
                        Map<String, Object> fileMsg = new HashMap<>();
                        fileMsg.put("role", "user");
                        fileMsg.put("file_name", file.getOriginalFilename());

                        String contentType = file.getContentType();
                        fileName = file.getOriginalFilename();

                        // 文本文件直接读取内容
                        if (isTextFile(contentType, fileName)) {
                            logger.info("Handling text file: {} ({})", fileName, contentType);
                            String content = readTextFileContent(file);
                            fileMsg.put("content", content);
                            logger.debug("Text file content (first 100 chars): {}",
                                    content.substring(0, Math.min(100, content.length())));
                        }
                        // 非文本文件使用Base64编码
                        else {
                            logger.info("Handling binary file: {} ({})", fileName, contentType);
                            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                            fileMsg.put("content", "data:" + contentType + ";base64," + base64);
                            logger.debug("Base64 content (first 100 chars): {}",
                                    base64.substring(0, Math.min(100, base64.length())));
                        }

                        messages.add(fileMsg);
                    } else {
                        logger.warn("Skipped empty file: {}", fileName);
                    }
                }
            }

            if (messages.isEmpty()) {
                logger.warn("No valid messages to send");
                return ResponseEntity.badRequest().body(Map.of("error", "请提供消息或上传文件"));
            }

            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);

            logger.info("Sending request to DeepSeek API with {} messages", messages.size());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + deepSeekApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.deepseek.com/v1/chat/completions", entity, Map.class);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("DeepSeek API response: HTTP {} ({} ms)",
                    response.getStatusCode(), duration);
            logger.debug("Response body: {}", response.getBody());

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            logger.error("DeepSeek API request failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "服务器错误: " + e.getMessage()));
        }
    }

    // 检测是否为文本文件
    private boolean isTextFile(String contentType, String fileName) {
        if (contentType != null) {
            return TEXT_MIME_TYPES.stream().anyMatch(contentType::contains);
        }
        // 根据文件名判断
        if (fileName != null) {
            String lowerName = fileName.toLowerCase();
            return lowerName.endsWith(".txt") ||
                    lowerName.endsWith(".csv") ||
                    lowerName.endsWith(".log") ||
                    lowerName.endsWith(".json") ||
                    lowerName.endsWith(".xml");
        }
        return false;
    }

    // 读取文本文件内容，自动检测编码
    private String readTextFileContent(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();

        // 检测可能的编码
        if (isValidEncoding(bytes, StandardCharsets.UTF_8) ||
                isValidEncoding(bytes, StandardCharsets.UTF_16) ||
                isValidEncoding(bytes, StandardCharsets.ISO_8859_1)) {
            return new String(bytes, detectEncoding(bytes));
        }

        // 尝试常见中文编码
        try {
            return new String(bytes, "GBK");
        } catch (Exception e) {
            // 最后尝试UTF-8
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    // 检测是否有效编码
    private boolean isValidEncoding(byte[] bytes, Charset charset) {
        return new String(bytes, charset).length() == new String(bytes, StandardCharsets.UTF_8).length();
    }

    // 简单的编码检测
    private Charset detectEncoding(byte[] bytes) {
        // 检查BOM标记
        if (bytes.length >= 3 && bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
            return StandardCharsets.UTF_8;
        }

        // 统计可打印字符比例
        int printableChars = 0;
        for (byte b : bytes) {
            if (b >= 0x20 && b < 0x7F) printableChars++; // ASCII可打印字符
        }

        float ratio = (float) printableChars / bytes.length;
        return ratio > 0.8 ? StandardCharsets.UTF_8 : Charset.forName("GBK");
    }
}