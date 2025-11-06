package com.mathpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mathpractice.entity.User;
import com.mathpractice.mapper.UserMapper;
import com.mathpractice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户表 服务实现类
 *
 * @author chidao
 * @since 2025-11-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        User user = baseMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        if (isUsernameExists(user.getUsername())) {
            return false;
        }
        return this.save(user);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return baseMapper.selectByRole(role);
    }

    @Override
    public List<User> getStudentsByClass(String className) {
        return baseMapper.selectStudentsByClass(className);
    }

    @Override
    public boolean isUsernameExists(String username) {
        Integer count = baseMapper.countByUsername(username);
        return count != null && count > 0;
    }
}