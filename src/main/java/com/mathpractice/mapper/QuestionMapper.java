package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 题目 Mapper 接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 根据ID查询题目基本信息（避免映射问题）
     */
    @Select("SELECT id, type_id, difficulty_id, subject, knowledge_point, content, analysis, created_by, created_at FROM questions WHERE id = #{id}")
    Question selectQuestionById(@Param("id") Integer id);

    /**
     * 根据类型ID获取题目
     */
    @Select("SELECT q.* FROM questions q WHERE q.type_id = #{typeId} ORDER BY q.created_at DESC")
    List<Question> selectByTypeId(@Param("typeId") Integer typeId);

    /**
     * 根据难度ID获取题目
     */
    @Select("SELECT q.* FROM questions q WHERE q.difficulty_id = #{difficultyId} ORDER BY q.created_at DESC")
    List<Question> selectByDifficultyId(@Param("difficultyId") Integer difficultyId);

    /**
     * 根据类型ID和难度ID获取题目
     */
    @Select("SELECT q.* FROM questions q WHERE q.type_id = #{typeId} AND q.difficulty_id = #{difficultyId} ORDER BY q.created_at DESC")
    List<Question> selectByTypeIdAndDifficultyId(@Param("typeId") Integer typeId,
                                                 @Param("difficultyId") Integer difficultyId);

    /**
     * 获取所有题目（包含关联信息）
     */
    @Select("SELECT q.*, qt.name as type_name, dl.name as difficulty_name " +
            "FROM questions q " +
            "LEFT JOIN question_types qt ON q.type_id = qt.id " +
            "LEFT JOIN difficulty_levels dl ON q.difficulty_id = dl.id " +
            "ORDER BY q.created_at DESC")
    List<Question> selectAllWithDetails();

    /**
     * 根据科目查询题目
     */
    @Select("SELECT q.* FROM questions q WHERE q.subject = #{subject} ORDER BY q.created_at DESC")
    List<Question> selectBySubject(@Param("subject") String subject);

    /**
     * 根据知识点查询题目
     */
    @Select("SELECT q.* FROM questions q WHERE q.knowledge_point LIKE CONCAT('%', #{knowledgePoint}, '%') ORDER BY q.created_at DESC")
    List<Question> selectByKnowledgePoint(@Param("knowledgePoint") String knowledgePoint);

    /**
     * 根据创建者查询题目
     */
    @Select("SELECT q.* FROM questions q WHERE q.created_by = #{createdBy} ORDER BY q.created_at DESC")
    List<Question> selectByCreatedBy(@Param("createdBy") Integer createdBy);

    /**
     * 根据ID列表获取题目
     */
    @Select({
            "<script>",
            "SELECT q.*, qt.name as type_name, dl.name as difficulty_name FROM questions q",
            "LEFT JOIN question_types qt ON q.type_id = qt.id",
            "LEFT JOIN difficulty_levels dl ON q.difficulty_id = dl.id",
            "WHERE q.id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "ORDER BY q.id",
            "</script>"
    })
    List<Question> selectByIdsWithDetails(@Param("ids") List<Integer> ids);

    /**
     * 搜索题目（按内容和知识点）
     */
    @Select("SELECT q.* FROM questions q WHERE q.content LIKE CONCAT('%', #{keyword}, '%') OR q.knowledge_point LIKE CONCAT('%', #{keyword}, '%') ORDER BY q.created_at DESC")
    List<Question> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 统计各类型题目数量
     */
    @Select("SELECT qt.name as type_name, COUNT(q.id) as count " +
            "FROM question_types qt " +
            "LEFT JOIN questions q ON qt.id = q.type_id " +
            "GROUP BY qt.id, qt.name " +
            "ORDER BY qt.id")
    List<Map<String, Object>> countByType();

    /**
     * 统计各难度题目数量
     */
    @Select("SELECT dl.name as difficulty_name, COUNT(q.id) as count " +
            "FROM difficulty_levels dl " +
            "LEFT JOIN questions q ON dl.id = q.difficulty_id " +
            "GROUP BY dl.id, dl.name " +
            "ORDER BY dl.level")
    List<Map<String, Object>> countByDifficulty();

    /**
     * 根据ID列表获取题目（用于试卷生成）
     */
    @Select({
            "<script>",
            "SELECT q.* FROM questions q",
            "WHERE q.id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "ORDER BY q.id",
            "</script>"
    })
    List<Question> selectByIds(@Param("ids") List<Integer> ids);
}