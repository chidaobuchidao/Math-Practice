package com.mathpractice.controller;

import com.mathpractice.dto.GeneratePaperRequest;
import com.mathpractice.entity.Paper;
import com.mathpractice.response.ApiResponse;
import com.mathpractice.service.PaperService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/papers")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;

    // 生成试卷（老师用）
    @PostMapping("/generate")
    public ApiResponse<Paper> generatePaper(@Valid @RequestBody GeneratePaperRequest request) {
        try {
            Paper paper = paperService.generatePaper(request);
            return ApiResponse.success(paper);
        } catch (Exception e) {
            return ApiResponse.error("生成试卷失败: " + e.getMessage());
        }
    }

    // 获取学生的试卷列表
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Paper>> getStudentPapers(@PathVariable Integer studentId) {
        try {
            List<Paper> papers = paperService.getStudentPapers(studentId);
            return ApiResponse.success(papers);
        } catch (Exception e) {
            return ApiResponse.error("获取试卷列表失败: " + e.getMessage());
        }
    }

    // 获取试卷详情（包含题目）
    @GetMapping("/{paperId}")
    public ApiResponse<Map<String, Object>> getPaperWithQuestions(@PathVariable Integer paperId) {
        try {
            Map<String, Object> paperDetail = paperService.getPaperWithQuestions(paperId);
            return ApiResponse.success(paperDetail);
        } catch (Exception e) {
            return ApiResponse.error("获取试卷详情失败: " + e.getMessage());
        }
    }

    // 删除试卷 - 简化逻辑
    @DeleteMapping("/{paperId}")
    public ApiResponse<Object> deletePaper(@PathVariable Integer paperId) {
        try {
            boolean removed = paperService.removeById(paperId);
            if (removed) {
                return ApiResponse.success("试卷删除成功");
            } else {
                return ApiResponse.error("试卷删除失败，试卷可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("删除试卷失败: " + e.getMessage());
        }
    }
}
