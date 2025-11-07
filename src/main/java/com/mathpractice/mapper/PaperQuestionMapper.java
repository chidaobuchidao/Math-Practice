package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.PaperQuestion;
import com.mathpractice.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaperQuestionMapper extends BaseMapper<PaperQuestion> {

    @Select("SELECT * FROM paper_questions WHERE paper_id = #{paperId}")
    List<PaperQuestion> selectByPaperId(@Param("paperId") Integer paperId);

    // 获取试卷的题目详情
    @Select("SELECT q.* FROM questions q " +
            "INNER JOIN paper_questions pq ON q.id = pq.question_id " +
            "WHERE pq.paper_id = #{paperId}")
    List<Question> selectQuestionsByPaperId(@Param("paperId") Integer paperId);
}