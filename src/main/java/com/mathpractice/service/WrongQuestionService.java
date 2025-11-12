package com.mathpractice.service;

import com.mathpractice.entity.WrongQuestion;

import java.util.List;
import java.util.Map;

public interface WrongQuestionService {

    /**
     * 获取学生的错题列表（包含题目详情）
     */
    List<Map<String, Object>> getWrongQuestionsWithDetail(Integer studentId);

    /**
     * 从错题集中移除题目
     */
    boolean removeFromWrongQuestions(Integer studentId, Integer questionId);

    /**
     * 统计学生的错题数量
     */
    int countWrongQuestions(Integer studentId);

    /**
     * 清空学生的错题集
     */
    boolean clearWrongQuestions(Integer studentId);
}