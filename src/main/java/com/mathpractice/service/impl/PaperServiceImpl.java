package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.dto.GeneratePaperRequest;
import com.mathpractice.dto.SubmitPaperRequest;
import com.mathpractice.entity.Paper;
import com.mathpractice.entity.PaperQuestion;
import com.mathpractice.entity.Question;
import com.mathpractice.exception.BusinessException;
import com.mathpractice.mapper.PaperMapper;
import com.mathpractice.mapper.PaperQuestionMapper;
import com.mathpractice.mapper.QuestionMapper;
import com.mathpractice.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mathpractice.entity.WrongQuestion;
import com.mathpractice.mapper.WrongQuestionMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    private final QuestionMapper questionMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final WrongQuestionMapper wrongQuestionMapper;

    @Override
    @Transactional
    public Paper generatePaper(GeneratePaperRequest request) {
        try {
            // 1. 验证题目是否存在
            List<Question> selectedQuestions = questionMapper.selectByIds(request.getQuestionIds());
            if (selectedQuestions.size() != request.getQuestionIds().size()) {
                throw new BusinessException("部分题目不存在，请检查题目ID");
            }

            // 2. 创建试卷记录
            Paper paper = new Paper();
            paper.setStudentId(request.getStudentId());
            paper.setTitle(request.getTitle());
            paper.setTotalQuestions(selectedQuestions.size());
            paper.setCorrectCount(0);
            paper.setScore(null);  // 改为 null 表示未完成
            paper.setTimeSpent(0);

            this.save(paper);

            // 3. 关联题目到试卷
            for (Question question : selectedQuestions) {
                PaperQuestion paperQuestion = new PaperQuestion();
                paperQuestion.setPaperId(paper.getId());
                paperQuestion.setQuestionId(question.getId());
                paperQuestion.setStudentAnswer(null);
                paperQuestion.setIsCorrect(false);

                paperQuestionMapper.insert(paperQuestion);
            }

            return paper;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("生成试卷失败: " + e.getMessage());
        }
    }

    // 修改 submitPaper 方法，在批改题目时记录错题
    @Override
    @Transactional
    public Paper submitPaper(Integer paperId, SubmitPaperRequest request) {
        // 1. 获取试卷
        Paper paper = this.getById(paperId);
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }

        // 2. 获取试卷关联的题目
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByPaperId(paperId);

        int correctCount = 0;

        // 3. 批改每道题目
        for (PaperQuestion paperQuestion : paperQuestions) {
            Integer questionId = paperQuestion.getQuestionId();
            Double studentAnswer = request.getAnswers().get(questionId);
            Question question = questionMapper.selectById(questionId);

            // 设置学生答案
            paperQuestion.setStudentAnswer(studentAnswer != null ? BigDecimal.valueOf(studentAnswer) : null);

            // 判断是否正确（考虑浮点数精度）
            boolean isCorrect = studentAnswer != null &&
                    question.getAnswer() != null &&
                    Math.abs(studentAnswer - question.getAnswer().doubleValue()) < 0.01;

            paperQuestion.setIsCorrect(isCorrect);

            if (isCorrect) {
                correctCount++;
            } else {
                // 记录错题 - 新增代码
                recordWrongQuestion(paper.getStudentId(), questionId, studentAnswer, paperId);
            }

            // 更新题目答案记录
            paperQuestionMapper.updateById(paperQuestion);
        }

        // 4. 更新试卷信息
        paper.setCorrectCount(correctCount);
        paper.setScore(request.getScore() != null ? request.getScore() :
                (double) correctCount / paper.getTotalQuestions() * 100);
        paper.setTimeSpent(request.getTimeSpent());

        this.updateById(paper);

        return paper;
    }

    @Override
    public List<Paper> getStudentPapers(Integer studentId) {
        return baseMapper.selectByStudentId(studentId);
    }

    @Override
    public List<Paper> getAllPapers() {
        return baseMapper.selectList(null);
    }
    @Override
    public Map<String, Object> getPaperWithQuestions(Integer paperId) {
        Map<String, Object> result = new HashMap<>();

        // 获取试卷信息
        Paper paper = this.getById(paperId);
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }
        result.put("paper", paper);

        // 获取试卷的题目列表
        List<Question> questions = paperQuestionMapper.selectQuestionsByPaperId(paperId);
        result.put("questions", questions);

        return result;
    }

    // 新增方法：记录错题
    private void recordWrongQuestion(Integer studentId, Integer questionId, Double wrongAnswer, Integer paperId) {
        try {
            WrongQuestion wrongQuestion = new WrongQuestion();
            wrongQuestion.setStudentId(studentId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setWrongAnswer(wrongAnswer != null ? BigDecimal.valueOf(wrongAnswer) : null);
            wrongQuestion.setPaperId(paperId);

            wrongQuestionMapper.insert(wrongQuestion);
        } catch (Exception e) {
            // 记录错题失败不影响主流程，但记录日志
            System.err.println("记录错题失败: " + e.getMessage());
        }
    }
}
