package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 错题记录表实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wrong_questions")
public class WrongQuestion {
    /**
     * 错题ID，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Integer studentId;

    /**
     * 题目ID
     */
    @TableField("question_id")
    private Integer questionId;

    /**
     * 学生的错误答案（文本格式，支持数字、分数、文本等）
     */
    @TableField("wrong_answer")
    private String wrongAnswer;

    /**
     * 关联的试卷ID
     */
    @TableField("paper_id")
    private Integer paperId;

    /**
     * 记录时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}