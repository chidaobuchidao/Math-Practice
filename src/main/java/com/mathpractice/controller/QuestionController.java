package com.mathpractice.controller;

import com.mathpractice.entity.Question;
import com.mathpractice.response.ApiResponse;
import com.mathpractice.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 题目控制器
 *
 * @author chidao
 * @since 2025-11-07
 */
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // 获取题目库（可按类型和难度筛选）
    @GetMapping("/bank")
    public ApiResponse<List<Question>> getQuestionBank(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty) {
        try {
            List<Question> questions = questionService.getQuestionBank(type, difficulty);
            return ApiResponse.success(questions);
        } catch (Exception e) {
            return ApiResponse.error("获取题目库失败: " + e.getMessage());
        }
    }

    // 获取所有题目
    @GetMapping("/all")
    public ApiResponse<List<Question>> getAllQuestions() {
        try {
            List<Question> questions = questionService.getAllQuestions();
            return ApiResponse.success(questions);
        } catch (Exception e) {
            return ApiResponse.error("获取题目失败: " + e.getMessage());
        }
    }

    // 获取题目选项（类型和难度）
    @GetMapping("/options")
    public ApiResponse<Map<String, Object>> getQuestionOptions() {
        try {
            Map<String, Object> options = questionService.getQuestionOptions();
            return ApiResponse.success(options);
        } catch (Exception e) {
            return ApiResponse.error("获取选项失败: " + e.getMessage());
        }
    }

    // 添加题目（老师用）
    @PostMapping("/add")
    public ApiResponse<Object> addQuestion(@RequestBody Question question) {
        try {
            boolean saved = questionService.save(question);
            if (saved) {
                return ApiResponse.success("题目添加成功");
            } else {
                return ApiResponse.error("题目添加失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("添加题目失败: " + e.getMessage());
        }
    }

    // 修改题目（老师用）
    // 获取单个题目详情
    @GetMapping("/{questionId}")
    public ApiResponse<Question> getQuestionById(@PathVariable Integer questionId) {
        try {
            Question question = questionService.getById(questionId);
            if (question != null) {
                return ApiResponse.success(question);
            } else {
                return ApiResponse.error("题目不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取题目失败: " + e.getMessage());
        }
    }
    @PutMapping("/{questionId}")
    public ApiResponse<Object> updateQuestion(@PathVariable Integer questionId, @RequestBody Question question) {
        try {
            boolean updated = questionService.updateById(question);
            if (updated) {
                return ApiResponse.success("题目修改成功");
            } else {
                return ApiResponse.error("题目修改失败，题目可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("修改题目失败: " + e.getMessage());
        }
    }

    // 删除题目 - 简化逻辑，只需要题目ID
    @DeleteMapping("/{questionId}")
    public ApiResponse<Object> deleteQuestion(@PathVariable Integer questionId) {
        try {
            boolean removed = questionService.removeById(questionId);
            if (removed) {
                return ApiResponse.success("题目删除成功");
            } else {
                return ApiResponse.error("题目删除失败，题目可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("删除题目失败: " + e.getMessage());
        }
    }
}
