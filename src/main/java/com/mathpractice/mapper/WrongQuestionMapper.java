package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.WrongQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {

    /**
     * 获取学生的错题列表（包含题目详情）- 关联新表
     */
    @Select("SELECT wq.*, q.content, q.type_id, q.difficulty_id, q.subject, q.knowledge_point, " +
            "qt.name as type_name, dl.name as difficulty_name, dl.code as difficulty_code, " +
            "qa.content as correct_answer " +
            "FROM wrong_questions wq " +
            "LEFT JOIN questions q ON wq.question_id = q.id " +
            "LEFT JOIN question_types qt ON q.type_id = qt.id " +
            "LEFT JOIN difficulty_levels dl ON q.difficulty_id = dl.id " +
            "LEFT JOIN question_answers qa ON q.id = qa.question_id AND qa.is_correct = 1 " +
            "WHERE wq.student_id = #{studentId} " +
            "ORDER BY wq.created_at DESC")
    List<Map<String, Object>> selectWrongQuestionsWithDetail(@Param("studentId") Integer studentId);

    /**
     * 根据题目ID和学生ID删除错题记录
     */
    @Delete("DELETE FROM wrong_questions WHERE student_id = #{studentId} AND question_id = #{questionId}")
    int deleteByStudentAndQuestion(@Param("studentId") Integer studentId, @Param("questionId") Integer questionId);

    /**
     * 统计学生的错题数量
     */
    @Select("SELECT COUNT(DISTINCT question_id) FROM wrong_questions WHERE student_id = #{studentId}")
    int countWrongQuestions(@Param("studentId") Integer studentId);

    /**
     * 检查是否已存在错题记录
     */
    @Select("SELECT COUNT(*) FROM wrong_questions WHERE student_id = #{studentId} AND question_id = #{questionId}")
    int existsWrongQuestion(@Param("studentId") Integer studentId, @Param("questionId") Integer questionId);

    /**
     * 插入错题记录（用于学生做题时自动记录）
     */
    @Insert("INSERT INTO wrong_questions (student_id, question_id, wrong_answer, paper_id) " +
            "VALUES (#{studentId}, #{questionId}, #{wrongAnswer}, #{paperId})")
    int insertWrongQuestion(@Param("studentId") Integer studentId,
                            @Param("questionId") Integer questionId,
                            @Param("wrongAnswer") String wrongAnswer,
                            @Param("paperId") Integer paperId);
}