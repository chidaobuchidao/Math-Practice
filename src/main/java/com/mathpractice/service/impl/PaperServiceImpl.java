package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.dto.GeneratePaperRequest;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    private final QuestionMapper questionMapper;
    private final PaperQuestionMapper paperQuestionMapper;

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
            paper.setScore(BigDecimal.ZERO);
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
    public List<Paper> getStudentPapers(Integer studentId) {
        return baseMapper.selectByStudentId(studentId);
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
}
