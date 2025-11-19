package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.entity.WrongQuestion;
import com.mathpractice.mapper.WrongQuestionMapper;
import com.mathpractice.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WrongQuestionServiceImpl extends ServiceImpl<WrongQuestionMapper, WrongQuestion> implements WrongQuestionService {

    private final WrongQuestionMapper wrongQuestionMapper;

    @Override
    public List<Map<String, Object>> getWrongQuestionsWithDetail(Integer studentId) {
        return wrongQuestionMapper.selectWrongQuestionsWithDetail(studentId);
    }

    @Override
    public boolean removeFromWrongQuestions(Integer studentId, Integer questionId) {
        int result = wrongQuestionMapper.deleteByStudentAndQuestion(studentId, questionId);
        return result > 0;
    }

    @Override
    public int countWrongQuestions(Integer studentId) {
        return wrongQuestionMapper.countWrongQuestions(studentId);
    }

    @Override
    public boolean clearWrongQuestions(Integer studentId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("student_id", studentId);
        return this.removeByMap(queryMap);
    }

    @Override
    public boolean existsWrongQuestion(Integer studentId, Integer questionId) {
        return wrongQuestionMapper.existsWrongQuestion(studentId, questionId) > 0;
    }

    @Override
    public boolean recordWrongQuestion(Integer studentId, Integer questionId, String wrongAnswer, Integer paperId) {
        // 检查是否已存在该错题记录，避免重复记录
        if (wrongQuestionMapper.existsWrongQuestion(studentId, questionId) > 0) {
            System.out.println("错题已存在，跳过记录 - 学生ID: " + studentId + ", 题目ID: " + questionId);
            return true; // 已存在，返回成功
        }

        int result = wrongQuestionMapper.insertWrongQuestion(studentId, questionId, wrongAnswer, paperId);
        boolean success = result > 0;

        if (success) {
            System.out.println("成功记录错题 - 学生ID: " + studentId + ", 题目ID: " + questionId);
        } else {
            System.err.println("记录错题失败 - 学生ID: " + studentId + ", 题目ID: " + questionId);
        }

        return success;
    }

    @Override
    @Transactional
    public boolean batchRecordWrongQuestions(Integer studentId, Map<Integer, String> wrongAnswers, Integer paperId) {
        try {
            int recordedCount = 0;
            int skippedCount = 0;

            for (Map.Entry<Integer, String> entry : wrongAnswers.entrySet()) {
                Integer questionId = entry.getKey();
                String wrongAnswer = entry.getValue();

                // 检查是否已存在，避免重复记录
                if (!existsWrongQuestion(studentId, questionId)) {
                    if (recordWrongQuestion(studentId, questionId, wrongAnswer, paperId)) {
                        recordedCount++;
                    }
                } else {
                    skippedCount++;
                }
            }

            System.out.println("批量记录错题完成 - 学生ID: " + studentId +
                    ", 新增记录: " + recordedCount +
                    ", 跳过重复: " + skippedCount);
            return true;
        } catch (Exception e) {
            System.err.println("批量记录错题失败: " + e.getMessage());
            throw new RuntimeException("批量记录错题失败: " + e.getMessage(), e);
        }
    }
}