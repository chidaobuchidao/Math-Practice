package com.mathpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathpractice.dto.GeneratePaperRequest;
import com.mathpractice.dto.SubmitPaperRequest;
import com.mathpractice.entity.Paper;

import java.util.List;
import java.util.Map;

public interface PaperService extends IService<Paper> {

    // 生成试卷（老师用）
    Paper generatePaper(GeneratePaperRequest request);

    // 提交试卷答案（新增）
    Paper submitPaper(Integer paperId, SubmitPaperRequest request);

    // 获取学生的试卷列表
    List<Paper> getStudentPapers(Integer studentId);

    // 获取所有试卷（老师用）
    List<Paper> getAllPapers();

    // 获取试卷详情（包含题目）
    Map<String, Object> getPaperWithQuestions(Integer paperId);
}