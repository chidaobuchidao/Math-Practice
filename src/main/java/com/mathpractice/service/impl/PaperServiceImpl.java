package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.dto.GeneratePaperRequest;
import com.mathpractice.dto.SubmitPaperRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mathpractice.entity.Paper;
import com.mathpractice.entity.PaperQuestion;
import com.mathpractice.entity.Question;
import com.mathpractice.entity.QuestionAnswer;
import com.mathpractice.entity.QuestionOption;
import com.mathpractice.entity.QuestionImage;
import com.mathpractice.exception.BusinessException;
import com.mathpractice.mapper.PaperMapper;
import com.mathpractice.mapper.PaperQuestionMapper;
import com.mathpractice.mapper.QuestionMapper;
import com.mathpractice.mapper.QuestionAnswerMapper;
import com.mathpractice.mapper.QuestionOptionMapper;
import com.mathpractice.mapper.QuestionImageMapper;
import com.mathpractice.service.PaperService;
import com.mathpractice.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    private final QuestionMapper questionMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionAnswerMapper questionAnswerMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionImageMapper questionImageMapper;
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
            Object studentAnswerObj = request.getAnswers().get(questionId);

            // 获取题目的正确答案
            QuestionAnswer correctAnswerInfo = getCorrectAnswerInfo(questionId);
            BigDecimal correctAnswer = correctAnswerInfo != null ? 
                    parseAnswerToBigDecimal(correctAnswerInfo.getContent(), correctAnswerInfo.getAnswerType()) : null;

            // 处理学生答案（支持数字和字符串）
            Double studentAnswerDouble = null;
            String studentAnswerString = null;
            
            if (studentAnswerObj != null) {
                if (studentAnswerObj instanceof Number) {
                    studentAnswerDouble = ((Number) studentAnswerObj).doubleValue();
                } else if (studentAnswerObj instanceof String) {
                    studentAnswerString = (String) studentAnswerObj;
                    // 尝试将字符串转换为数字
                    try {
                        studentAnswerDouble = Double.parseDouble(studentAnswerString);
                    } catch (NumberFormatException e) {
                        // 如果无法转换为数字，可能是选择题的选项键（A, B, C, D）
                        // 对于多选题（如 "A,B,C"），保留原始字符串用于后续比较
                        // 将选项键转换为数字存储：A=1, B=2, C=3, D=4, ...
                        if (studentAnswerString != null && studentAnswerString.length() > 0) {
                            // 去除空格并转换为大写
                            String cleanAnswer = studentAnswerString.trim().toUpperCase();
                            if (cleanAnswer.length() > 0) {
                                char firstChar = cleanAnswer.charAt(0);
                                if (firstChar >= 'A' && firstChar <= 'Z') {
                                    // 单个选项键（如 'A'）或多选题的第一个选项键（如 'A,B'）
                                    // 注意：对于多选题，这里只存储第一个选项的数字，完整答案在 studentAnswerString 中保留
                                    studentAnswerDouble = (double) (firstChar - 'A' + 1);
                                }
                            }
                        }
                    }
                }
            }

            // 设置学生答案到数据库（存储为 BigDecimal 或 null）
            // 对于选择题，选项键已转换为数字（A=1, B=2, C=3, D=4）
            paperQuestion.setStudentAnswer(studentAnswerDouble != null ? 
                    BigDecimal.valueOf(studentAnswerDouble) : null);

            // 判断是否正确
            boolean isCorrect = false;
            if (correctAnswerInfo != null) {
                String answerType = correctAnswerInfo.getAnswerType();
                
                if ("text".equals(answerType) || "single".equals(answerType) || "multiple".equals(answerType)) {
                    // 文本类型或选择题：直接比较字符串内容
                    String correctContent = correctAnswerInfo.getContent();
                    String studentContent = studentAnswerString != null ? studentAnswerString : 
                            (studentAnswerDouble != null ? convertNumberToOptionKey(studentAnswerDouble.intValue()) : null);
                    
                    // 对于多选题，需要比较所有选项（排序后比较）
                    if ("multiple".equals(answerType) && studentContent != null && correctContent != null) {
                        // 分割并去除每个选项的空格
                        String[] studentOptions = studentContent.split(",");
                        String[] correctOptions = correctContent.split(",");
                        // 去除空格并转换为大写
                        for (int i = 0; i < studentOptions.length; i++) {
                            studentOptions[i] = studentOptions[i].trim().toUpperCase();
                        }
                        for (int i = 0; i < correctOptions.length; i++) {
                            correctOptions[i] = correctOptions[i].trim().toUpperCase();
                        }
                        Arrays.sort(studentOptions);
                        Arrays.sort(correctOptions);
                        isCorrect = Arrays.equals(studentOptions, correctOptions);
                    } else {
                        isCorrect = correctContent != null && correctContent.equals(studentContent);
                    }
                } else {
                    // 数字类型或分数类型：数值比较
                    isCorrect = studentAnswerDouble != null &&
                            correctAnswer != null &&
                            Math.abs(studentAnswerDouble - correctAnswer.doubleValue()) < 0.01;
                }
            }

            paperQuestion.setIsCorrect(isCorrect);

            if (isCorrect) {
                correctCount++;
            } else {
                // 记录错题信息（先收集，最后批量处理）
                String wrongAnswerStr = studentAnswerString != null ? studentAnswerString :
                        (studentAnswerDouble != null ? formatWrongAnswer(studentAnswerDouble) : "未作答");
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
        // 计算得分并保留整数
        // 防止除零错误：如果题目数为0，得分设为0
        double calculatedScore;
        if (paper.getTotalQuestions() == null || paper.getTotalQuestions() == 0) {
            calculatedScore = request.getScore() != null ? request.getScore() : 0.0;
        } else {
            calculatedScore = request.getScore() != null ? request.getScore() :
                    (double) correctCount / paper.getTotalQuestions() * 100;
        }
        paper.setScore((double) Math.round(calculatedScore)); // 四舍五入到整数
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
     * 获取题目的正确答案信息
     */
    private QuestionAnswer getCorrectAnswerInfo(Integer questionId) {
        try {
            // 从 question_answers 表获取正确答案
            List<QuestionAnswer> answers = questionAnswerMapper.selectByQuestionId(questionId);

            if (answers != null && !answers.isEmpty()) {
                // 查找所有正确答案（is_correct = true）
                List<QuestionAnswer> correctAnswers = new ArrayList<>();
                for (QuestionAnswer answer : answers) {
                    if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                        correctAnswers.add(answer);
                    }
                }
                
                if (correctAnswers.isEmpty()) {
                    return null;
                }
                
                // 如果只有一个正确答案，直接返回
                if (correctAnswers.size() == 1) {
                    return correctAnswers.get(0);
                }
                
                // 对于多选题，可能有多个正确答案行，需要合并为一个
                // 检查答案类型
                String answerType = correctAnswers.get(0).getAnswerType();
                if ("multiple".equals(answerType)) {
                    // 合并所有正确答案内容为逗号分隔的字符串
                    List<String> answerContents = new ArrayList<>();
                    for (QuestionAnswer answer : correctAnswers) {
                        if (answer.getContent() != null && !answer.getContent().trim().isEmpty()) {
                            answerContents.add(answer.getContent().trim());
                        }
                    }
                    // 创建合并后的答案对象
                    QuestionAnswer mergedAnswer = new QuestionAnswer();
                    mergedAnswer.setAnswerType(answerType);
                    mergedAnswer.setContent(String.join(",", answerContents));
                    mergedAnswer.setIsCorrect(true);
                    mergedAnswer.setSortOrder(0);
                    return mergedAnswer;
                } else {
                    // 非多选题，返回第一个正确答案
                    return correctAnswers.get(0);
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
     * 将答案内容解析为 BigDecimal（用于数值比较）
     */
    private BigDecimal parseAnswerToBigDecimal(String content, String answerType) {
        if (content == null) return null;
        
        try {
            switch (answerType) {
                case "number":
                    return new BigDecimal(content);
                case "fraction":
                    // 处理分数格式，如 "1/2"
                    return parseFraction(content);
                case "text":
                case "single":
                case "multiple":
                    // 文本类型或选择题，无法转换为数值
                    return null;
                default:
                    // 尝试直接解析为数字
                    try {
                        return new BigDecimal(content);
                    } catch (NumberFormatException e) {
                        return null;
                    }
            }
        } catch (Exception e) {
            return null;
        }
    }

    
    /**
     * 将数字转换为选项键（1=A, 2=B, 3=C, 4=D, ...）
     */
    private String convertNumberToOptionKey(int number) {
        if (number >= 1 && number <= 26) {
            return String.valueOf((char) ('A' + number - 1));
        }
        return String.valueOf(number);
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
            
            // 如果是选择题，加载选项
            if (question.getTypeId() != null && (question.getTypeId() == 1 || question.getTypeId() == 2)) {
                QueryWrapper<QuestionOption> optionWrapper = new QueryWrapper<>();
                optionWrapper.eq("question_id", question.getId()).orderByAsc("sort_order");
                List<QuestionOption> options = questionOptionMapper.selectList(optionWrapper);
                questionDetail.put("options", options);
            }
            
            // 加载图片
            QueryWrapper<QuestionImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("question_id", question.getId());
            List<QuestionImage> images = questionImageMapper.selectList(imageWrapper);
            questionDetail.put("images", images);

            // 学生答案和是否正确
            PaperQuestion paperQuestion = paperQuestionMap.get(question.getId());
            if (paperQuestion != null) {
                // 处理学生答案：如果是选择题，需要将数字转换回选项键
                Object studentAnswerValue = null;
                if (paperQuestion.getStudentAnswer() != null) {
                    Double answerDouble = paperQuestion.getStudentAnswer().doubleValue();
                    // 判断是否为选择题
                    if (question.getTypeId() != null && (question.getTypeId() == 1 || question.getTypeId() == 2)) {
                        // 选择题：将数字转换回选项键（1=A, 2=B, 3=C, 4=D, ...）
                        int optionIndex = answerDouble.intValue();
                        if (optionIndex >= 1 && optionIndex <= 26) {
                            // 对于多选题，需要检查是否有多个选项（通过检查原始答案字符串）
                            // 但由于数据库只存储数字，我们只能返回第一个选项键
                            // 注意：多选题的完整答案可能需要从其他地方获取，这里先返回单个选项键
                            studentAnswerValue = String.valueOf((char) ('A' + optionIndex - 1));
                        } else {
                            // 如果不是有效的选项索引，直接返回数字字符串
                            studentAnswerValue = answerDouble.toString();
                        }
                    } else {
                        // 非选择题：直接返回数字
                        studentAnswerValue = answerDouble;
                    }
                }
                questionDetail.put("studentAnswer", studentAnswerValue);
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