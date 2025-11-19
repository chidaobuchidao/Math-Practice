package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

// QuestionAnswer.java - 题目答案实体
@Data
@TableName("question_answers")
public class QuestionAnswer {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("question_id")
    private Integer questionId;

    @TableField("answer_type")
    private String answerType; // single, multiple, text, number, fraction

    private String content;
    private Boolean isCorrect;
    private Integer sortOrder;
    private Timestamp createdAt;
}
