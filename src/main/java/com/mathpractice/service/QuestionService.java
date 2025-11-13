package com.mathpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathpractice.entity.Question;

import java.util.List;
import java.util.Map;

public interface QuestionService extends IService<Question> {
    // 批量保存题目
    boolean saveBatch(List<Question> questions);

    // 获取题目库（按条件筛选）
    List<Question> getQuestionBank(String type, String difficulty);

    // 获取所有题目
    List<Question> getAllQuestions();

    // 获取题目类型和难度选项
    Map<String, Object> getQuestionOptions();
}