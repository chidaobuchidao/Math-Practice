// QuestionGenerationRequest.java
package com.mathpractice.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionGenerationRequest {
    // 题目数量
    private Integer count;

    // 题目类型：AddAndSub, MulAndDiv, Mixed
    private List<String> types;

    // 难度：easy, medium, hard
    private List<String> difficulties;

    // 数字范围配置
    private NumberRange numberRange;

    // 创建者ID（老师ID）
    private Integer createdBy;

    @Data
    public static class NumberRange {
        // 加减法数字范围
        private Integer addSubMin = 1;
        private Integer addSubMax = 100;

        // 乘法数字范围
        private Integer multiplicationMin = 1;
        private Integer multiplicationMax = 12;

        // 除法数字范围
        private Integer divisionMin = 1;
        private Integer divisionMax = 12;

        // 混合运算数字范围
        private Integer mixedMin = 1;
        private Integer mixedMax = 100;
    }
}