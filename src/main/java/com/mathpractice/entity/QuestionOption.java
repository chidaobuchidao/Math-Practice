package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

// QuestionOption.java - 题目选项实体
@Data
@TableName("question_options")
public class QuestionOption {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("question_id")
    private Integer questionId;

    @TableField("option_key")
    private String optionKey; // A, B, C, D

    private String content;
    private Integer sortOrder;
    private Timestamp createdAt;
}
