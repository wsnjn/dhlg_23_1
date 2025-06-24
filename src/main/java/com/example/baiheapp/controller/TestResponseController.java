package com.example.baiheapp.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class TestResponseController {

    private final JdbcTemplate jdbcTemplate;

    // 通过构造函数注入JdbcTemplate
    public TestResponseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 工具方法：按 created_at 分组数据
    private List<Map<String, Object>> groupByCreatedAt(List<Map<String, Object>> rows) {
        // 创建一个按日期分组的Map
        Map<String, List<Map<String, Object>>> groups = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            // 获取日期并转换为字符串（格式：YYYY-MM-DD）
            Timestamp createdAt = (Timestamp) row.get("created_at");
            String dateKey = createdAt != null ? createdAt.toLocalDateTime().toLocalDate().toString() : "未知日期";

            // 添加到对应的日期分组
            groups.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(row);
        }

        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groups.entrySet()) {
            Map<String, Object> group = new HashMap<>();
            group.put("created_at", entry.getKey());
            group.put("responses", entry.getValue());
            result.add(group);
        }

        return result;
    }

    // 获取AD8测试结果
    @GetMapping("/user/ad8_responses")
    public Map<String, Object> getAD8Responses(
            @RequestParam(required = false) String hospitalId,
            @RequestParam(required = false) String patientName) {

        // 验证参数
        if (hospitalId == null || hospitalId.isEmpty() || patientName == null || patientName.isEmpty()) {
            return Map.of(
                    "success", false,
                    "message", "hospitalId和patientName是必填参数"
            );
        }

        try {
            // 执行查询
            String sql = "SELECT * FROM ad8_responses WHERE hospital_id = ? AND patient_name = ? ORDER BY created_at DESC";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, hospitalId, patientName);

            // 分组处理
            List<Map<String, Object>> groupedData = groupByCreatedAt(rows);

            return Map.of(
                    "success", true,
                    "data", groupedData,
                    "message", "AD8测试结果获取成功"
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "获取AD8测试结果失败: " + e.getMessage()
            );
        }
    }

    // 获取PSQI测试结果
    @GetMapping("/user/psqi_responses")
    public Map<String, Object> getPSQIResponses(
            @RequestParam(required = false) String hospitalId,
            @RequestParam(required = false) String patientName) {

        // 验证参数
        if (hospitalId == null || hospitalId.isEmpty() || patientName == null || patientName.isEmpty()) {
            return Map.of(
                    "success", false,
                    "message", "hospitalId和patientName是必填参数"
            );
        }

        try {
            // 执行查询
            String sql = "SELECT * FROM psqi_responses WHERE hospital_id = ? AND patient_name = ? ORDER BY created_at DESC";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, hospitalId, patientName);

            // 分组处理
            List<Map<String, Object>> groupedData = groupByCreatedAt(rows);

            return Map.of(
                    "success", true,
                    "data", groupedData,
                    "message", "PSQI测试结果获取成功"
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "获取PSQI测试结果失败: " + e.getMessage()
            );
        }
    }
}