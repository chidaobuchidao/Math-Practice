package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

// QuestionType.java - 题目类型实体
@Data
@TableName("question_types")
public class QuestionType {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String code;
    private String description;
    private Timestamp createdAt;
}
