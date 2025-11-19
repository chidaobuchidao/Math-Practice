package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.QuestionOption;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目选项 Mapper 接口
 */
@Mapper
public interface QuestionOptionMapper extends BaseMapper<QuestionOption> {

    /**
     * 根据题目ID查询选项列表
     */
    @Select("SELECT * FROM question_options WHERE question_id = #{questionId} ORDER BY sort_order ASC")
    List<QuestionOption> selectByQuestionId(Integer questionId);

    /**
     * 根据题目ID和选项键查询选项
     */
    @Select("SELECT * FROM question_options WHERE question_id = #{questionId} AND option_key = #{optionKey}")
    QuestionOption selectByQuestionIdAndKey(@Param("questionId") Integer questionId,
                                            @Param("optionKey") String optionKey);

    /**
     * 根据题目ID删除所有选项
     */
    @Delete("DELETE FROM question_options WHERE question_id = #{questionId}")
    int deleteByQuestionId(Integer questionId);

    /**
     * 批量插入选项
     */
    int insertBatch(@Param("list") List<QuestionOption> options);
}