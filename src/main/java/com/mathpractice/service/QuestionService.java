package com.mathpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathpractice.entity.DifficultyLevel;
import com.mathpractice.entity.Question;
import com.mathpractice.entity.QuestionType;

import java.util.List;
import java.util.Map;

public interface QuestionService extends IService<Question> {

    // 获取题目库（可按类型和难度筛选）
    List<Question> getQuestionBank(String type, String difficulty);

    // 获取所有题目
    List<Question> getAllQuestions();

    // 获取题目选项（类型和难度）
    Map<String, Object> getQuestionOptions();

    // 获取题目详情（包含关联数据）
    Question getQuestionDetail(Integer questionId);

    // 保存题目（包含答案和选项）
    boolean saveQuestionWithDetails(Question question);

    // 更新题目（包含答案和选项）
    boolean updateQuestionWithDetails(Question question);

    // 获取所有题目类型
    List<QuestionType> getAllQuestionTypes();

    // 获取所有难度等级
    List<DifficultyLevel> getAllDifficultyLevels();

    // 批量删除题目
    boolean removeQuestionWithDetails(Integer questionId);
}