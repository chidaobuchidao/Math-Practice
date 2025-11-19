package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.entity.*;
import com.mathpractice.mapper.*;
import com.mathpractice.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionTypeMapper questionTypeMapper;
    private final DifficultyLevelMapper difficultyLevelMapper;
    private final QuestionAnswerMapper questionAnswerMapper;
    private final QuestionOptionMapper questionOptionMapper;

    @Override
    public List<Question> getQuestionBank(String type, String difficulty) {
        if (type != null && !type.isEmpty() && difficulty != null && !difficulty.isEmpty()) {
            // 根据类型代码和难度代码查询
            QuestionType questionType = questionTypeMapper.selectByCode(type);
            DifficultyLevel difficultyLevel = difficultyLevelMapper.selectByCode(difficulty);
            if (questionType != null && difficultyLevel != null) {
                return questionMapper.selectByTypeIdAndDifficultyId(questionType.getId(), difficultyLevel.getId());
            }
            return List.of(); // 如果类型或难度不存在，返回空列表
        } else if (type != null && !type.isEmpty()) {
            QuestionType questionType = questionTypeMapper.selectByCode(type);
            if (questionType != null) {
                return questionMapper.selectByTypeId(questionType.getId());
            }
            return List.of();
        } else if (difficulty != null && !difficulty.isEmpty()) {
            DifficultyLevel difficultyLevel = difficultyLevelMapper.selectByCode(difficulty);
            if (difficultyLevel != null) {
                return questionMapper.selectByDifficultyId(difficultyLevel.getId());
            }
            return List.of();
        }
        return questionMapper.selectAllWithDetails();
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionMapper.selectAllWithDetails();
    }

    @Override
    public Map<String, Object> getQuestionOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("types", getAllQuestionTypes());
        options.put("difficulties", getAllDifficultyLevels());
        return options;
    }

    @Override
    public Question getQuestionDetail(Integer questionId) {
        // 使用自定义查询方法，避免自动映射问题
        Question question = questionMapper.selectQuestionById(questionId);
        if (question != null) {
            // 加载题目类型
            QuestionType questionType = questionTypeMapper.selectById(question.getTypeId());
            question.setQuestionType(questionType);

            // 加载难度等级
            DifficultyLevel difficultyLevel = difficultyLevelMapper.selectById(question.getDifficultyId());
            question.setDifficultyLevel(difficultyLevel);

            // 加载答案
            QueryWrapper<QuestionAnswer> answerWrapper = new QueryWrapper<>();
            answerWrapper.eq("question_id", questionId).orderByAsc("sort_order");
            List<QuestionAnswer> answers = questionAnswerMapper.selectList(answerWrapper);
            question.setAnswers(answers);

            // 如果是选择题，加载选项
            if (questionType != null &&
                    ("single_choice".equals(questionType.getCode()) ||
                            "multiple_choice".equals(questionType.getCode()))) {
                QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
                optionWrapper.eq("question_id", questionId).orderByAsc("sort_order");
                List<QuestionOption> options = questionOptionMapper.selectList(optionWrapper);
                question.setOptions(options);
            }
        }
        return question;
    }

    @Override
    @Transactional
    public boolean saveQuestionWithDetails(Question question) {
        // 保存题目基本信息
        int result = questionMapper.insert(question);
        if (result > 0 && question.getId() != null) {
            // 保存答案
            if (question.getAnswers() != null) {
                for (QuestionAnswer answer : question.getAnswers()) {
                    answer.setQuestionId(question.getId());
                    questionAnswerMapper.insert(answer);
                }
            }
            // 保存选项（如果是选择题）
            if (question.getOptions() != null) {
                for (QuestionOption option : question.getOptions()) {
                    option.setQuestionId(question.getId());
                    questionOptionMapper.insert(option);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateQuestionWithDetails(Question question) {
        // 更新题目基本信息
        int result = questionMapper.updateById(question);
        if (result > 0) {
            // 删除原有答案和选项
            QueryWrapper<QuestionAnswer> answerWrapper = new QueryWrapper<>();
            answerWrapper.eq("question_id", question.getId());
            questionAnswerMapper.delete(answerWrapper);

            QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
            optionWrapper.eq("question_id", question.getId());
            questionOptionMapper.delete(optionWrapper);

            // 保存新的答案
            if (question.getAnswers() != null) {
                for (QuestionAnswer answer : question.getAnswers()) {
                    answer.setQuestionId(question.getId());
                    questionAnswerMapper.insert(answer);
                }
            }
            // 保存新的选项
            if (question.getOptions() != null) {
                for (QuestionOption option : question.getOptions()) {
                    option.setQuestionId(question.getId());
                    questionOptionMapper.insert(option);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeQuestionWithDetails(Integer questionId) {
        // 删除答案
        QueryWrapper<QuestionAnswer> answerWrapper = new QueryWrapper<>();
        answerWrapper.eq("question_id", questionId);
        questionAnswerMapper.delete(answerWrapper);

        // 删除选项
        QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
        optionWrapper.eq("question_id", questionId);
        questionOptionMapper.delete(optionWrapper);

        // 删除题目
        return questionMapper.deleteById(questionId) > 0;
    }

    @Override
    public List<QuestionType> getAllQuestionTypes() {
        return questionTypeMapper.selectList(new QueryWrapper<QuestionType>().orderByAsc("id"));
    }

    @Override
    public List<DifficultyLevel> getAllDifficultyLevels() {
        return difficultyLevelMapper.selectList(new QueryWrapper<DifficultyLevel>().orderByAsc("level"));
    }
}