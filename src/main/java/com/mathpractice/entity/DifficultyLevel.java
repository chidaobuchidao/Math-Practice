package com.mathpractice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

// DifficultyLevel.java - 难度等级实体
@Data
@TableName("difficulty_levels")
public class DifficultyLevel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String code;
    private Integer level;
    private String description;
    private Timestamp createdAt;
}
