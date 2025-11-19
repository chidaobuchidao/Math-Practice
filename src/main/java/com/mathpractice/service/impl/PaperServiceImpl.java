package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.dto.GeneratePaperRequest;
import com.mathpractice.dto.SubmitPaperRequest;
import com.mathpractice.entity.Paper;
import com.mathpractice.entity.PaperQuestion;
import com.mathpractice.entity.Question;
import com.mathpractice.entity.QuestionAnswer;
import com.mathpractice.exception.BusinessException;
import com.mathpractice.mapper.PaperMapper;
import com.mathpractice.mapper.PaperQuestionMapper;
import com.mathpractice.mapper.QuestionMapper;
import com.mathpractice.mapper.QuestionAnswerMapper;
import com.mathpractice.service.PaperService;
import com.mathpractice.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    private final QuestionMapper questionMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionAnswerMapper questionAnswerMapper;
    private final WrongQuestionService wrongQuestionService; // 使用服务而非Mapper

    @Override
    @Transactional
    public Paper generatePaper(GeneratePaperRequest request) {
        try {
            // 1. 验证题目是否存在 - 使用新的questions表
            List<Question> selectedQuestions = questionMapper.selectByIdsWithDetails(request.getQuestionIds());
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
        Map<Integer, String> wrongAnswers = new HashMap<>(); // 用于批量记录错题

        // 3. 批改每道题目
        for (PaperQuestion paperQuestion : paperQuestions) {
            Integer questionId = paperQuestion.getQuestionId();
            Double studentAnswer = request.getAnswers().get(questionId);

            // 获取题目的正确答案
            BigDecimal correctAnswer = getCorrectAnswer(questionId);

            // 设置学生答案
            paperQuestion.setStudentAnswer(studentAnswer != null ? BigDecimal.valueOf(studentAnswer) : null);

            // 判断是否正确（考虑浮点数精度）
            boolean isCorrect = studentAnswer != null &&
                    correctAnswer != null &&
                    Math.abs(studentAnswer - correctAnswer.doubleValue()) < 0.01;

            paperQuestion.setIsCorrect(isCorrect);

            if (isCorrect) {
                correctCount++;
            } else {
                // 记录错题信息（先收集，最后批量处理）
                String wrongAnswerStr = studentAnswer != null ?
                        formatWrongAnswer(studentAnswer) : "未作答";
                wrongAnswers.put(questionId, wrongAnswerStr);
            }

            // 更新题目答案记录
            paperQuestionMapper.updateById(paperQuestion);
        }

        // 4. 批量记录错题（自动去重）
        if (!wrongAnswers.isEmpty()) {
            wrongQuestionService.batchRecordWrongQuestions(paper.getStudentId(), wrongAnswers, paperId);
        }

        // 5. 更新试卷信息
        paper.setCorrectCount(correctCount);
        paper.setScore(request.getScore() != null ? request.getScore() :
                (double) correctCount / paper.getTotalQuestions() * 100);
        paper.setTimeSpent(request.getTimeSpent());

        this.updateById(paper);

        return paper;
    }

    /**
     * 格式化错误答案为字符串
     */
    private String formatWrongAnswer(Double answer) {
        if (answer == null) return "未作答";

        // 如果是整数，去掉小数部分
        if (answer == Math.floor(answer)) {
            return String.valueOf(answer.intValue());
        } else {
            // 保留2位小数
            return String.format("%.2f", answer);
        }
    }

    /**
     * 获取题目的正确答案
     */
    private BigDecimal getCorrectAnswer(Integer questionId) {
        try {
            // 从 question_answers 表获取正确答案
            List<QuestionAnswer> answers = questionAnswerMapper.selectByQuestionId(questionId);

            if (answers != null && !answers.isEmpty()) {
                // 查找正确答案（is_correct = true）
                for (QuestionAnswer answer : answers) {
                    if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                        // 根据答案类型处理
                        String answerType = answer.getAnswerType();
                        String content = answer.getContent();

                        switch (answerType) {
                            case "number":
                                return new BigDecimal(content);
                            case "fraction":
                                // 处理分数格式，如 "1/2"
                                return parseFraction(content);
                            case "text":
                                // 文本答案无法直接比较数值，返回null
                                return null;
                            default:
                                return new BigDecimal(content);
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            // 记录错误但不中断流程
            System.err.println("获取题目答案失败，题目ID: " + questionId + ", 错误: " + e.getMessage());
            return null;
        }
    }

    /**
     * 解析分数格式为小数
     */
    private BigDecimal parseFraction(String fraction) {
        try {
            if (fraction.contains("/")) {
                String[] parts = fraction.split("/");
                if (parts.length == 2) {
                    double numerator = Double.parseDouble(parts[0]);
                    double denominator = Double.parseDouble(parts[1]);
                    return BigDecimal.valueOf(numerator / denominator);
                }
            }
            // 如果不是分数格式，尝试直接解析为数字
            return new BigDecimal(fraction);
        } catch (Exception e) {
            System.err.println("解析分数失败: " + fraction);
            return null;
        }
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

        // 获取试卷题目关联信息（包含学生答案）
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectByPaperId(paperId);

        // 创建题目ID到PaperQuestion的映射
        Map<Integer, PaperQuestion> paperQuestionMap = new HashMap<>();
        for (PaperQuestion pq : paperQuestions) {
            paperQuestionMap.put(pq.getQuestionId(), pq);
        }

        // 构建题目详情列表
        List<Map<String, Object>> questionDetails = new ArrayList<>();
        for (Question question : questions) {
            Map<String, Object> questionDetail = new HashMap<>();

            // 题目基本信息
            questionDetail.put("id", question.getId());
            questionDetail.put("content", question.getContent());
            questionDetail.put("analysis", question.getAnalysis());
            questionDetail.put("typeId", question.getTypeId());
            questionDetail.put("difficultyId", question.getDifficultyId());
            questionDetail.put("subject", question.getSubject());
            questionDetail.put("knowledgePoint", question.getKnowledgePoint());
            questionDetail.put("createdAt", question.getCreatedAt());

            // 加载题目答案
            List<QuestionAnswer> answers = questionAnswerMapper.selectByQuestionId(question.getId());
            questionDetail.put("answers", answers);

            // 学生答案和是否正确
            PaperQuestion paperQuestion = paperQuestionMap.get(question.getId());
            if (paperQuestion != null) {
                // 将BigDecimal类型的studentAnswer转换为Double，便于前端处理
                questionDetail.put("studentAnswer",
                        paperQuestion.getStudentAnswer() != null ?
                                paperQuestion.getStudentAnswer().doubleValue() : null);
                questionDetail.put("isCorrect", paperQuestion.getIsCorrect());
            } else {
                questionDetail.put("studentAnswer", null);
                questionDetail.put("isCorrect", false);
            }

            questionDetails.add(questionDetail);
        }

        result.put("questions", questionDetails);

        return result;
    }
}