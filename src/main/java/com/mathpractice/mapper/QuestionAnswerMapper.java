package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.QuestionAnswer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 题目答案 Mapper 接口
 */
@Mapper
public interface QuestionAnswerMapper extends BaseMapper<QuestionAnswer> {

    /**
     * 根据题目ID查询答案列表
     */
    @Select("SELECT * FROM question_answers WHERE question_id = #{questionId} ORDER BY sort_order ASC")
    List<QuestionAnswer> selectByQuestionId(Integer questionId);

    /**
     * 根据题目ID和答案类型查询答案
     */
    @Select("SELECT * FROM question_answers WHERE question_id = #{questionId} AND answer_type = #{answerType} ORDER BY sort_order ASC")
    List<QuestionAnswer> selectByQuestionIdAndType(@Param("questionId") Integer questionId,
                                                   @Param("answerType") String answerType);

    /**
     * 根据题目ID删除所有答案
     */
    @Delete("DELETE FROM question_answers WHERE question_id = #{questionId}")
    int deleteByQuestionId(Integer questionId);

    /**
     * 查询题目的正确答案
     */
    @Select("SELECT * FROM question_answers WHERE question_id = #{questionId} AND is_correct = 1 ORDER BY sort_order ASC")
    List<QuestionAnswer> selectCorrectAnswers(Integer questionId);

    /**
     * 批量插入答案
     */
    int insertBatch(@Param("list") List<QuestionAnswer> answers);
}