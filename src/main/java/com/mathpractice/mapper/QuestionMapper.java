package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    // 根据类型获取题目
    @Select("SELECT * FROM questions WHERE type = #{type}")
    List<Question> selectByType(@Param("type") String type);

    // 根据难度获取题目
    @Select("SELECT * FROM questions WHERE difficulty = #{difficulty}")
    List<Question> selectByDifficulty(@Param("difficulty") String difficulty);

    // 根据类型和难度获取题目
    @Select("SELECT * FROM questions WHERE type = #{type} AND difficulty = #{difficulty}")
    List<Question> selectByTypeAndDifficulty(@Param("type") String type,
                                             @Param("difficulty") String difficulty);

    // 获取所有题目
    @Select("SELECT * FROM questions ORDER BY created_at DESC")
    List<Question> selectAll();

    // 根据ID列表获取题目
    @Select({
            "<script>",
            "SELECT * FROM questions WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            " ORDER BY id",
            "</script>"
    })
    List<Question> selectByIds(@Param("ids") List<Integer> ids);
}