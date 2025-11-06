package com.mathpractice.controller;

import com.mathpractice.entity.User;
import com.mathpractice.response.ApiResponse;
import com.mathpractice.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户表 前端控制器
 *
 * @author chidao
 * @since 2025-11-06
 */
@RestController
@RequestMapping("/User")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public ApiResponse<Object> addUser(@RequestBody User user) {
        boolean isSaved = userService.save(user);
        return ApiResponse.success();
    }

    @GetMapping("/list")
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userService.list());
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            return ApiResponse.success(loginUser);
        }
        return ApiResponse.error("用户名或密码错误");
    }

    @PostMapping("/register")
    public ApiResponse<Object> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return ApiResponse.success();
        }
        return ApiResponse.error("用户名已存在");
    }

    @GetMapping("/students")
    public ApiResponse<List<User>> getStudents() {
        List<User> students = userService.getUsersByRole("student");
        return ApiResponse.success(students);
    }

    @GetMapping("/teachers")
    public ApiResponse<List<User>> getTeachers() {
        List<User> teachers = userService.getUsersByRole("teacher");
        return ApiResponse.success(teachers);
    }

    @GetMapping("/class/{className}")
    public ApiResponse<List<User>> getStudentsByClass(@PathVariable String className) {
        List<User> students = userService.getStudentsByClass(className);
        return ApiResponse.success(students);
    }

    @GetMapping("/checkUsername")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);
        return ApiResponse.success(exists);
    }

    @PutMapping("/update")
    public ApiResponse<Object> updateUser(@RequestBody User user) {
        boolean isUpdated = userService.updateById(user);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Object> deleteUser(@PathVariable Integer id) {
        boolean isRemoved = userService.removeById(id);
        return ApiResponse.success();
    }
}