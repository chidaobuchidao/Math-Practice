package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.entity.Question;
import com.mathpractice.mapper.QuestionMapper;
import com.mathpractice.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final QuestionMapper questionMapper;

    // 使用构造器注入替代字段注入
    public QuestionServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public List<Question> getQuestionBank(String type, String difficulty) {
        if (type != null && difficulty != null) {
            return questionMapper.selectByTypeAndDifficulty(type, difficulty);
        } else if (type != null) {
            return questionMapper.selectByType(type);
        } else if (difficulty != null) {
            return questionMapper.selectByDifficulty(difficulty);
        } else {
            return questionMapper.selectAll();
        }
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionMapper.selectAll();
    }

    @Override
    public Map<String, Object> getQuestionOptions() {
        Map<String, Object> options = new HashMap<>();

        // 题目类型选项
        options.put("types", new String[]{"AddAdnSub", "MulAndDiv", "Mixed"});

        // 难度等级选项
        options.put("difficulties", new String[]{"easy", "medium", "hard"});

        return options;
    }
}
