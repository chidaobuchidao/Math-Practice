package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.entity.WrongQuestion;
import com.mathpractice.mapper.WrongQuestionMapper;
import com.mathpractice.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}