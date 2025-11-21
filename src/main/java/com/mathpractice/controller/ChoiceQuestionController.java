package com.mathpractice.controller;

import com.mathpractice.dto.ChoiceQuestionRequest;
import com.mathpractice.entity.Question;
import com.mathpractice.entity.QuestionAnswer;
import com.mathpractice.entity.QuestionImage;
import com.mathpractice.entity.QuestionOption;
import com.mathpractice.response.ApiResponse;
import com.mathpractice.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 选择题制作控制器
 * 专门用于创建和管理选择题
 */
@RestController
@RequestMapping("/api/choice-questions")
@RequiredArgsConstructor
public class ChoiceQuestionController {

    private final QuestionService questionService;

    /**
     * 创建单选题
     */
    @PostMapping("/single")
    public ApiResponse<Object> createSingleChoiceQuestion(@RequestBody ChoiceQuestionRequest request) {
        try {
            // 验证请求
            validateRequest(request, true);

            // 构建Question对象
            Question question = buildQuestion(request, 1); // 1是单选题的type_id

            // 保存题目
            boolean saved = questionService.saveQuestionWithDetails(question);
            if (saved) {
                return ApiResponse.success("单选题创建成功");
            } else {
                return ApiResponse.error("单选题创建失败");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("创建单选题失败: " + e.getMessage());
        }
    }

    /**
     * 创建多选题
     */
    @PostMapping("/multiple")
    public ApiResponse<Object> createMultipleChoiceQuestion(@RequestBody ChoiceQuestionRequest request) {
        try {
            // 验证请求
            validateRequest(request, false);

            // 构建Question对象
            Question question = buildQuestion(request, 2); // 2是多选题的type_id

            // 保存题目
            boolean saved = questionService.saveQuestionWithDetails(question);
            if (saved) {
                return ApiResponse.success("多选题创建成功");
            } else {
                return ApiResponse.error("多选题创建失败");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("创建多选题失败: " + e.getMessage());
        }
    }

    /**
     * 更新选择题
     */
    @PutMapping("/{questionId}")
    public ApiResponse<Object> updateChoiceQuestion(
            @PathVariable Integer questionId,
            @RequestBody ChoiceQuestionRequest request) {
        try {
            // 验证请求
            if (request.getTypeId() == null) {
                return ApiResponse.error("题目类型ID不能为空");
            }
            if (request.getTypeId() == 1) {
                validateRequest(request, true);
            } else if (request.getTypeId() == 2) {
                validateRequest(request, false);
            } else {
                return ApiResponse.error("无效的题目类型ID，只支持单选题(1)和多选题(2)");
            }

            // 构建Question对象
            Question question = buildQuestion(request, request.getTypeId());
            question.setId(questionId);

            // 更新题目
            boolean updated = questionService.updateQuestionWithDetails(question);
            if (updated) {
                return ApiResponse.success("选择题更新成功");
            } else {
                return ApiResponse.error("选择题更新失败，题目可能不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新选择题失败: " + e.getMessage());
        }
    }

    /**
     * 获取选择题详情
     */
    @GetMapping("/{questionId}")
    public ApiResponse<Question> getChoiceQuestion(@PathVariable Integer questionId) {
        try {
            Question question = questionService.getQuestionDetail(questionId);
            if (question != null) {
                // 验证是否为选择题
                if (question.getQuestionType() != null &&
                        ("single_choice".equals(question.getQuestionType().getCode()) ||
                                "multiple_choice".equals(question.getQuestionType().getCode()))) {
                    return ApiResponse.success(question);
                } else {
                    return ApiResponse.error("该题目不是选择题");
                }
            } else {
                return ApiResponse.error("题目不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取选择题失败: " + e.getMessage());
        }
    }

    /**
     * 构建Question对象
     */
    private Question buildQuestion(ChoiceQuestionRequest request, Integer typeId) {
        Question question = new Question();
        question.setTypeId(typeId);
        question.setDifficultyId(request.getDifficultyId());
        question.setSubject(request.getSubject());
        question.setKnowledgePoint(request.getKnowledgePoint());
        question.setContent(request.getContent());
        question.setAnalysis(request.getAnalysis());
        question.setCreatedBy(request.getCreatedBy());

        // 构建选项列表
        question.setOptions(buildOptions(request.getOptions()));

        // 构建答案列表
        question.setAnswers(buildAnswers(request.getCorrectAnswers(), typeId));

        // 构建图片列表
        question.setImages(buildImages(request.getImages()));

        return question;
    }

    /**
     * 构建选项列表
     */
    private List<QuestionOption> buildOptions(List<ChoiceQuestionRequest.OptionItem> optionItems) {
        if (optionItems == null || optionItems.isEmpty()) {
            return new ArrayList<>();
        }
        return optionItems.stream()
                .map(item -> {
                    QuestionOption option = new QuestionOption();
                    option.setOptionKey(item.getOptionKey());
                    option.setContent(item.getContent());
                    option.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : 0);
                    return option;
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建答案列表
     */
    private List<QuestionAnswer> buildAnswers(List<String> correctAnswers, Integer typeId) {
        if (correctAnswers == null || correctAnswers.isEmpty()) {
            return new ArrayList<>();
        }
        String answerType = typeId == 1 ? "single" : "multiple";
        List<QuestionAnswer> answers = new ArrayList<>();
        
        if (typeId == 1) {
            // 单选题：每个答案创建一个 QuestionAnswer 行
            for (int i = 0; i < correctAnswers.size(); i++) {
                QuestionAnswer answer = new QuestionAnswer();
                answer.setAnswerType(answerType);
                answer.setContent(correctAnswers.get(i));
                answer.setIsCorrect(true);
                answer.setSortOrder(i);
                answers.add(answer);
            }
        } else {
            // 多选题：将所有答案合并为一个逗号分隔的字符串，存储在单个 QuestionAnswer 行中
            // 这样与 PaperServiceImpl 中的比较逻辑保持一致
            String mergedContent = String.join(",", correctAnswers);
            QuestionAnswer answer = new QuestionAnswer();
            answer.setAnswerType(answerType);
            answer.setContent(mergedContent);
            answer.setIsCorrect(true);
            answer.setSortOrder(0);
            answers.add(answer);
        }
        
        return answers;
    }

    /**
     * 构建图片列表
     */
    private List<QuestionImage> buildImages(List<ChoiceQuestionRequest.ImageItem> imageItems) {
        if (imageItems == null || imageItems.isEmpty()) {
            return new ArrayList<>();
        }
        return imageItems.stream()
                .map(item -> {
                    QuestionImage image = new QuestionImage();
                    image.setImagePath(item.getImagePath());
                    image.setImageName(item.getImageName());
                    return image;
                })
                .collect(Collectors.toList());
    }

    /**
     * 验证请求
     */
    private void validateRequest(ChoiceQuestionRequest request, boolean isSingle) {
        // 基础字段验证
        validateBasicFields(request);
        
        // 选项验证
        validateOptions(request.getOptions());
        
        // 答案验证
        validateAnswers(request.getCorrectAnswers(), request.getOptions(), isSingle);
    }

    /**
     * 验证基础字段
     */
    private void validateBasicFields(ChoiceQuestionRequest request) {
        if (request.getDifficultyId() == null) {
            throw new IllegalArgumentException("难度ID不能为空");
        }
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("科目不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("题目内容不能为空");
        }
    }

    /**
     * 验证选项
     */
    private void validateOptions(List<ChoiceQuestionRequest.OptionItem> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("选项不能为空");
        }
        if (options.size() < 2) {
            throw new IllegalArgumentException("至少需要2个选项");
        }

        // 验证选项键是否唯一
        List<String> optionKeys = new ArrayList<>();
        for (ChoiceQuestionRequest.OptionItem option : options) {
            if (option.getOptionKey() == null || option.getOptionKey().trim().isEmpty()) {
                throw new IllegalArgumentException("选项键不能为空");
            }
            if (optionKeys.contains(option.getOptionKey())) {
                throw new IllegalArgumentException("选项键不能重复: " + option.getOptionKey());
            }
            optionKeys.add(option.getOptionKey());
        }
    }

    /**
     * 验证答案
     */
    private void validateAnswers(List<String> correctAnswers, 
                                  List<ChoiceQuestionRequest.OptionItem> options, 
                                  boolean isSingle) {
        if (correctAnswers == null || correctAnswers.isEmpty()) {
            throw new IllegalArgumentException("正确答案不能为空");
        }
        
        if (isSingle && correctAnswers.size() > 1) {
            throw new IllegalArgumentException("单选题只能有一个正确答案");
        }
        
        if (!isSingle && correctAnswers.size() < 2) {
            throw new IllegalArgumentException("多选题至少需要两个正确答案");
        }

        // 获取所有选项键
        List<String> optionKeys = options.stream()
                .map(ChoiceQuestionRequest.OptionItem::getOptionKey)
                .collect(Collectors.toList());

        // 验证正确答案是否在选项中
        for (String answer : correctAnswers) {
            if (!optionKeys.contains(answer)) {
                throw new IllegalArgumentException("正确答案不在选项中: " + answer);
            }
        }
    }
}

