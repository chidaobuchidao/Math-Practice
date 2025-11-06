package com.mathpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathpractice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表mapper接口
 *
 * @author chidao
 * @since 2025-11-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM users WHERE role = #{role} ORDER BY created_at DESC")
    List<User> selectByRole(@Param("role") String role);

    @Select("SELECT * FROM users WHERE class = #{className} AND role = 'student' ORDER BY username")
    List<User> selectStudentsByClass(@Param("className") String className);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    Integer countByUsername(@Param("username") String username);
}