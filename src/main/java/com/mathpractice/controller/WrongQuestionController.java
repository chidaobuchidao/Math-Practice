package com.mathpractice.controller;

import com.mathpractice.response.ApiResponse;
import com.mathpractice.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 错题控制器
 *
 * @author chidao
 * @since 2025-11-07
 */
@RestController
@RequestMapping("/wrong-questions")
@RequiredArgsConstructor
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    /**
     * 获取学生的错题列表
     */
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Map<String, Object>>> getWrongQuestions(@PathVariable Integer studentId) {
        try {
            System.out.println("收到获取错题列表请求，studentId: " + studentId);

            if (studentId == null || studentId <= 0) {
                return ApiResponse.error("学生ID无效");
            }

            List<Map<String, Object>> wrongQuestions = wrongQuestionService.getWrongQuestionsWithDetail(studentId);
            return ApiResponse.success(wrongQuestions);
        } catch (Exception e) {
            System.err.println("获取错题列表失败: " + e.getMessage());
            return ApiResponse.error("获取错题列表失败: " + e.getMessage());
        }
    }

    /**
     * 从错题集中移除题目
     */
    @DeleteMapping("/student/{studentId}/question/{questionId}")
    public ApiResponse<Object> removeWrongQuestion(
            @PathVariable Integer studentId,
            @PathVariable Integer questionId) {
        try {
            boolean success = wrongQuestionService.removeFromWrongQuestions(studentId, questionId);
            if (success) {
                return ApiResponse.success("题目已从错题集中移除");
            } else {
                return ApiResponse.error("移除失败，题目可能不存在于错题集中");
            }
        } catch (Exception e) {
            return ApiResponse.error("移除错题失败: " + e.getMessage());
        }
    }

    /**
     * 清空学生的错题集
     */
    @DeleteMapping("/student/{studentId}/clear")
    public ApiResponse<Object> clearWrongQuestions(@PathVariable Integer studentId) {
        try {
            boolean success = wrongQuestionService.clearWrongQuestions(studentId);
            if (success) {
                return ApiResponse.success("错题集已清空");
            } else {
                return ApiResponse.error("清空失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("清空错题集失败: " + e.getMessage());
        }
    }

    /**
     * 统计学生的错题数量
     */
    @GetMapping("/student/{studentId}/count")
    public ApiResponse<Integer> countWrongQuestions(@PathVariable Integer studentId) {
        try {
            int count = wrongQuestionService.countWrongQuestions(studentId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error("统计错题数量失败: " + e.getMessage());
        }
    }
}