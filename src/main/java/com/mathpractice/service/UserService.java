package com.mathpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathpractice.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    User login(String username, String password);

    boolean register(User user);

    List<User> getUsersByRole(String role);

    List<User> getStudentsByClass(String className);

    boolean isUsernameExists(String username);
}