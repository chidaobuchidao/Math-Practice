package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 试卷题目实体
 *
 * @author chidao
 * @since 2025-11-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("paper_questions")
public class PaperQuestion {
    /**
     * 关联ID，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 试卷ID
     */
    @TableField("paper_id")
    private Integer paperId;

    /**
     * 题目ID
     */
    @TableField("question_id")
    private Integer questionId;

    /**
     * 学生答案（保留2位小数）
     */
    @TableField("student_answer")
    private BigDecimal studentAnswer;

    /**
     * 是否答对：0-错误，1-正确
     */
    @TableField("is_correct")
    private Boolean isCorrect;
}