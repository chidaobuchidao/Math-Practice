package com.mathpractice.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitPaperRequest {
    private Map<Integer, Object> answers; // 题目ID -> 学生答案（支持数字、字符串等类型）
    private Integer timeSpent; // 用时（秒）
    private Double score; // 得分
}