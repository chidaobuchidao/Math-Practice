package com.mathpractice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mathpractice.entity.User;
import com.mathpractice.exception.BusinessException;
import com.mathpractice.response.ApiResponse;
import com.mathpractice.response.ResponseCode;
import com.mathpractice.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户表 前端控制器
 *
 * @author chidao
 * @since 2025-11-07
 */
@RestController
@RequestMapping("/User")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public ApiResponse<Object> addUser(@RequestBody User user) {
        try {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, user.getUsername());
            if (userService.count(queryWrapper) > 0) {
                return ApiResponse.error(ResponseCode.USERNAME_EXISTS.getCode(),
                        ResponseCode.USERNAME_EXISTS.getMessage());
            }
            boolean saved = userService.save(user);
            if (saved) {
                return ApiResponse.success("用户添加成功");
            } else {
                return ApiResponse.error("用户添加失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("添加用户时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ApiResponse<PageInfo<User>> list(User user,
                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(user.getUsername() != null, User::getUsername, user.getUsername());
            queryWrapper.like(user.getRole() != null, User::getRole, user.getRole());
            queryWrapper.like(user.getUserClass() != null, User::getUserClass, user.getUserClass());

            PageHelper.startPage(pageNum, pageSize);
            List<User> userList = userService.list(queryWrapper);
            PageInfo<User> userPageInfo = new PageInfo<>(userList);

            return ApiResponse.success(userPageInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表时发生错误: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody User user) {
        try {
            User loginUser = userService.login(user.getUsername(), user.getPassword());
            if (loginUser != null) {
                return ApiResponse.success(loginUser);
            }
            return ApiResponse.error("用户名或密码错误");
        } catch (Exception e) {
            return ApiResponse.error("登录时发生错误: " + e.getMessage());
        }
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public ApiResponse<Object> register(@RequestBody User user) {
        try {
            boolean success = userService.register(user);
            if (success) {
                return ApiResponse.success("注册成功");
            }
            return ApiResponse.error("用户名已存在");
        } catch (Exception e) {
            return ApiResponse.error("注册时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/students")
    public ApiResponse<List<User>> getStudents() {
        try {
            List<User> students = userService.getUsersByRole("student");
            return ApiResponse.success(students);
        } catch (Exception e) {
            return ApiResponse.error("获取学生列表时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/teachers")
    public ApiResponse<List<User>> getTeachers() {
        try {
            List<User> teachers = userService.getUsersByRole("teacher");
            return ApiResponse.success(teachers);
        } catch (Exception e) {
            return ApiResponse.error("获取教师列表时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/class/{className}")
    public ApiResponse<List<User>> getStudentsByClass(@PathVariable String className) {
        try {
            List<User> students = userService.getStudentsByClass(className);
            return ApiResponse.success(students);
        } catch (Exception e) {
            return ApiResponse.error("获取班级学生列表时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/checkUsername")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        try {
            boolean exists = userService.isUsernameExists(username);
            return ApiResponse.success(exists);
        } catch (Exception e) {
            return ApiResponse.error("检查用户名时发生错误: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ApiResponse<Object> updateUser(@RequestBody User user) {
        try {
            if (user.getId() == null) {
                return ApiResponse.error("用户ID不能为空");
            }

            boolean isUpdated = userService.updateById(user);
            if (isUpdated) {
                return ApiResponse.success("用户更新成功");
            } else {
                return ApiResponse.error("用户更新失败，用户可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新用户时发生错误: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Object> deleteUser(@PathVariable Integer id) {
        try {
            if (id == null) {
                return ApiResponse.error("用户ID不能为空");
            }

            boolean isRemoved = userService.removeById(id);
            if (isRemoved) {
                return ApiResponse.success("用户删除成功");
            } else {
                return ApiResponse.error("用户删除失败，用户可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("删除用户时发生错误: " + e.getMessage());
        }
    }
}
