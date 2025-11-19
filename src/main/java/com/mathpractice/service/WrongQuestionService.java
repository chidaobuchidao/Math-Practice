package com.mathpractice.service;

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

    /**
     * 检查是否已存在错题记录
     */
    boolean existsWrongQuestion(Integer studentId, Integer questionId);

    /**
     * 记录错题（学生做题时调用，自动去重）
     */
    boolean recordWrongQuestion(Integer studentId, Integer questionId, String wrongAnswer, Integer paperId);

    /**
     * 批量记录错题（试卷提交时调用，自动去重）
     */
    boolean batchRecordWrongQuestions(Integer studentId, Map<Integer, String> wrongAnswers, Integer paperId);
}