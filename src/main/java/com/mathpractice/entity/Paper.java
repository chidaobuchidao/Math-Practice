package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 试卷表实体
 *
 * @author chidao
 * @since 2025-11-06
 */
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("papers")
public class Paper {
    /**
     * 试卷ID，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Integer studentId;

    /**
     * 试卷标题
     */
    @TableField("title")
    private String title;

    /**
     * 题目总数
     */
    @TableField("total_questions")
    private Integer totalQuestions;

    /**
     * 答对题目数量
     */
    @TableField("correct_count")
    private Integer correctCount;

    /**
     * 得分（满分100分）
     */
    @TableField("score")
    private Double score;

    /**
     * 花费时间（单位：秒）
     */
    @TableField("time_spent")
    private Integer timeSpent;

    /**
     * 创建时间（开始答题时间）
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}