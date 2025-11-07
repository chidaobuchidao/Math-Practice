package com.mathpractice.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitPaperRequest {
    private Map<Integer, Double> answers; // 题目ID -> 学生答案
    private Integer timeSpent; // 用时（秒）
    private Double score; // 得分
}