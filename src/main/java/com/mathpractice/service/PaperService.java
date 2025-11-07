package com.mathpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathpractice.dto.GeneratePaperRequest;
import com.mathpractice.entity.Paper;

import java.util.List;
import java.util.Map;

public interface PaperService extends IService<Paper> {

    // 生成试卷（老师用）
    Paper generatePaper(GeneratePaperRequest request);

    // 获取学生的试卷列表
    List<Paper> getStudentPapers(Integer studentId);

    // 获取试卷详情（包含题目）
    Map<String, Object> getPaperWithQuestions(Integer paperId);
}