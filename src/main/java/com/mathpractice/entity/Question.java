package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 题目库实体
 *
 * @author chidao
 * @since 2025-11-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("questions")
public class Question {
    /**
     * 题目ID，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 题目内容
     */
    @TableField("content")
    private String content;

    /**
     * 题目类型：AddAndSub-加减运算，MulAndDiv-乘除运算，Mixed-混合运算
     */
    @TableField("type")
    private String type;

    /**
     * 难度等级：easy-简单，medium-中等，hard-困难
     */
    @TableField("difficulty")
    private String difficulty;

    /**
     * 标准答案（保留2位小数）
     */
    @TableField("answer")
    private BigDecimal answer;

    /**
     * 创建者ID（老师ID）
     */
    @TableField("created_by")
    private Integer createdBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}