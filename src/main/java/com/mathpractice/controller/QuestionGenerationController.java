// QuestionGenerationController.java
package com.mathpractice.controller;

import com.mathpractice.dto.QuestionGenerationRequest;
import com.mathpractice.entity.Question;
import com.mathpractice.response.ApiResponse;
import com.mathpractice.service.QuestionService;
import com.mathpractice.util.QuestionGeneratorTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionGenerationController {

    private final QuestionGeneratorTool questionGeneratorTool;
    private final QuestionService questionService;

    /**
     * 生成题目并保存到数据库
     */
    @PostMapping("/generate")
    public ApiResponse<List<Question>> generateQuestions(@RequestBody QuestionGenerationRequest request) {
        try {
            log.info("收到题目生成请求: {}", request);

            // 参数验证
            if (request.getCount() == null || request.getCount() <= 0) {
                return ApiResponse.error("题目数量必须大于0");
            }

            if (request.getCount() > 100) {
                return ApiResponse.error("单次生成题目数量不能超过100");
            }

            // 生成题目
            List<Question> questions = questionGeneratorTool.generateQuestions(request);

            // 批量保存题目及其关联数据（答案存储在question_answers表中）
            int successCount = 0;
            int failCount = 0;
            for (Question question : questions) {
                try {
                    // 使用saveQuestionWithDetails方法保存题目和答案
                    // 答案会通过外键question_id保存到question_answers表
                    boolean saved = questionService.saveQuestionWithDetails(question);
                    if (saved) {
                        successCount++;
                    } else {
                        failCount++;
                        log.warn("保存题目失败: {}", question.getContent());
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("保存题目时发生异常: {}", question.getContent(), e);
                }
            }

            if (successCount > 0) {
                log.info("成功生成并保存 {}/{} 道题目", successCount, questions.size());
                if (failCount > 0) {
                    return ApiResponse.success(questions, 
                        String.format("成功生成 %d 道题目，%d 道保存失败", successCount, failCount));
                } else {
                    return ApiResponse.success(questions, "成功生成并保存 " + successCount + " 道题目");
                }
            } else {
                return ApiResponse.error("保存题目到数据库失败");
            }

        } catch (Exception e) {
            log.error("生成题目失败: {}", e.getMessage(), e);
            return ApiResponse.error("生成题目失败: " + e.getMessage());
        }
    }

    /**
     * 快速生成默认题目
     */
    @PostMapping("/generate/quick")
    public ApiResponse<List<Question>> generateQuickQuestions(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer createdBy) {

        try {
            QuestionGenerationRequest request = new QuestionGenerationRequest();
            request.setCount(count);
            request.setCreatedBy(createdBy);

            // 设置默认范围
            QuestionGenerationRequest.NumberRange range = new QuestionGenerationRequest.NumberRange();
            request.setNumberRange(range);

            List<Question> questions = questionGeneratorTool.generateQuestions(request);
            
            // 批量保存题目及其关联数据（答案存储在question_answers表中）
            int successCount = 0;
            for (Question question : questions) {
                try {
                    // 使用saveQuestionWithDetails方法保存题目和答案
                    // 答案会通过外键question_id保存到question_answers表
                    if (questionService.saveQuestionWithDetails(question)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("保存题目时发生异常: {}", question.getContent(), e);
                }
            }

            if (successCount > 0) {
                return ApiResponse.success(questions, "快速生成并保存 " + successCount + " 道题目");
            } else {
                return ApiResponse.error("保存题目失败");
            }

        } catch (Exception e) {
            log.error("快速生成题目失败: {}", e.getMessage(), e);
            return ApiResponse.error("快速生成题目失败: " + e.getMessage());
        }
    }
}