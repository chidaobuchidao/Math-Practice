package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {

    /**
     * 获取学生的错题列表（包含题目详情）
     */
    @Select("SELECT wq.*, q.content, q.type, q.difficulty, q.answer " +
            "FROM wrong_questions wq " +
            "LEFT JOIN questions q ON wq.question_id = q.id " +
            "WHERE wq.student_id = #{studentId} " +
            "ORDER BY wq.created_at DESC")
    List<Map<String, Object>> selectWrongQuestionsWithDetail(@Param("studentId") Integer studentId);

    /**
     * 根据题目ID和学生ID删除错题记录
     */
    @Select("DELETE FROM wrong_questions WHERE student_id = #{studentId} AND question_id = #{questionId}")
    int deleteByStudentAndQuestion(@Param("studentId") Integer studentId, @Param("questionId") Integer questionId);

    /**
     * 统计学生的错题数量
     */
    @Select("SELECT COUNT(DISTINCT question_id) FROM wrong_questions WHERE student_id = #{studentId}")
    int countWrongQuestions(@Param("studentId") Integer studentId);
}