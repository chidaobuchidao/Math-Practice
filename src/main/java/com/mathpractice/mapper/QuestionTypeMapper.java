package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.QuestionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目类型 Mapper 接口
 */
@Mapper
public interface QuestionTypeMapper extends BaseMapper<QuestionType> {

    /**
     * 根据类型代码查询题目类型
     */
    @Select("SELECT * FROM question_types WHERE code = #{code}")
    QuestionType selectByCode(String code);

    /**
     * 获取所有可用的题目类型（按ID排序）
     */
    @Select("SELECT * FROM question_types ORDER BY id ASC")
    List<QuestionType> selectAllTypes();
}