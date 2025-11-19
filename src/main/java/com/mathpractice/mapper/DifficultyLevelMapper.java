package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.DifficultyLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 难度等级 Mapper 接口
 */
@Mapper
public interface DifficultyLevelMapper extends BaseMapper<DifficultyLevel> {

    /**
     * 根据难度代码查询难度等级
     */
    @Select("SELECT * FROM difficulty_levels WHERE code = #{code}")
    DifficultyLevel selectByCode(String code);

    /**
     * 根据难度级别查询难度等级
     */
    @Select("SELECT * FROM difficulty_levels WHERE level = #{level}")
    DifficultyLevel selectByLevel(Integer level);

    /**
     * 获取所有难度等级（按级别排序）
     */
    @Select("SELECT * FROM difficulty_levels ORDER BY level ASC")
    List<DifficultyLevel> selectAllDifficulties();
}