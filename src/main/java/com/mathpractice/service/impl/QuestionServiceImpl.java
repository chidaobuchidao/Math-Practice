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
    private final QuestionImageMapper questionImageMapper;

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
        List<Question> questions = questionMapper.selectAllWithDetails();
        // 为每个题目加载选项和答案
        for (Question question : questions) {
            loadQuestionDetails(question);
        }
        return questions;
    }
    
    /**
     * 加载题目的详细信息（选项、答案、图片）
     */
    private void loadQuestionDetails(Question question) {
        if (question == null || question.getId() == null) {
            return;
        }
        
        Integer questionId = question.getId();
        
        // 加载答案
        QueryWrapper<QuestionAnswer> answerWrapper = new QueryWrapper<>();
        answerWrapper.eq("question_id", questionId).orderByAsc("sort_order");
        List<QuestionAnswer> answers = questionAnswerMapper.selectList(answerWrapper);
        question.setAnswers(answers);
        
        // 如果是选择题，加载选项
        if (question.getTypeId() != null && (question.getTypeId() == 1 || question.getTypeId() == 2)) {
            QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
            optionWrapper.eq("question_id", questionId).orderByAsc("sort_order");
            List<QuestionOption> options = questionOptionMapper.selectList(optionWrapper);
            question.setOptions(options);
        }
        
        // 加载图片
        QueryWrapper<QuestionImage> imageWrapper = new QueryWrapper<>();
        imageWrapper.eq("question_id", questionId);
        List<QuestionImage> images = questionImageMapper.selectList(imageWrapper);
        question.setImages(images);
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

            // 加载图片
            QueryWrapper<QuestionImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("question_id", questionId);
            List<QuestionImage> images = questionImageMapper.selectList(imageWrapper);
            question.setImages(images);
        }
        return question;
    }

    @Override
    @Transactional
    public boolean saveQuestionWithDetails(Question question) {
        // 保存题目基本信息
        int result = questionMapper.insert(question);
        if (result > 0 && question.getId() != null) {
            Integer questionId = question.getId();
            
            // 保存答案
            saveAnswers(questionId, question.getAnswers());
            
            // 保存选项（如果是选择题）
            saveOptions(questionId, question.getOptions());
            
            // 保存图片
            saveImages(questionId, question.getImages());
            
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
            Integer questionId = question.getId();
            
            // 删除原有关联数据
            deleteRelatedData(questionId);
            
            // 保存新的关联数据
            saveAnswers(questionId, question.getAnswers());
            saveOptions(questionId, question.getOptions());
            saveImages(questionId, question.getImages());
            
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeQuestionWithDetails(Integer questionId) {
        // 删除关联数据（由于设置了CASCADE，删除题目时会自动删除，但显式删除更清晰）
        deleteRelatedData(questionId);
        
        // 删除题目
        return questionMapper.deleteById(questionId) > 0;
    }

    /**
     * 保存答案
     */
    private void saveAnswers(Integer questionId, List<QuestionAnswer> answers) {
        if (answers != null && !answers.isEmpty()) {
            for (QuestionAnswer answer : answers) {
                answer.setQuestionId(questionId);
                questionAnswerMapper.insert(answer);
            }
        }
    }

    /**
     * 保存选项
     */
    private void saveOptions(Integer questionId, List<QuestionOption> options) {
        if (options != null && !options.isEmpty()) {
            for (QuestionOption option : options) {
                option.setQuestionId(questionId);
                questionOptionMapper.insert(option);
            }
        }
    }

    /**
     * 保存图片
     */
    private void saveImages(Integer questionId, List<QuestionImage> images) {
        if (images != null && !images.isEmpty()) {
            for (QuestionImage image : images) {
                image.setQuestionId(questionId);
                questionImageMapper.insert(image);
            }
        }
    }

    /**
     * 删除关联数据（答案、选项、图片）
     */
    private void deleteRelatedData(Integer questionId) {
        // 删除答案
        QueryWrapper<QuestionAnswer> answerWrapper = new QueryWrapper<>();
        answerWrapper.eq("question_id", questionId);
        questionAnswerMapper.delete(answerWrapper);

        // 删除选项
        QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
        optionWrapper.eq("question_id", questionId);
        questionOptionMapper.delete(optionWrapper);

        // 删除图片
        QueryWrapper<QuestionImage> imageWrapper = new QueryWrapper<>();
        imageWrapper.eq("question_id", questionId);
        questionImageMapper.delete(imageWrapper);
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