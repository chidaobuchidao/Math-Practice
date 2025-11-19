package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@TableName("questions")
public class Question {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("type_id")
    private Integer typeId;

    @TableField("difficulty_id")
    private Integer difficultyId;

    private String subject;

    @TableField("knowledge_point")
    private String knowledgePoint;

    private String content;
    private String analysis;

    @TableField("created_by")
    private Integer createdBy;

    @TableField("created_at")
    private Timestamp createdAt;

    // 关联字段 - 必须标记为不存在于表中
    @TableField(exist = false)
    private QuestionAnswer questionAnswer;  // 如果存在这个字段，确保有 exist = false

    @TableField(exist = false)
    private List<QuestionAnswer> answers;

    @TableField(exist = false)
    private List<QuestionOption> options;

    @TableField(exist = false)
    private QuestionType questionType;

    @TableField(exist = false)
    private DifficultyLevel difficultyLevel;
}