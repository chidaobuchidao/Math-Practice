package com.mathpractice.controller;

import com.mathpractice.response.ApiResponse;
import com.mathpractice.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileUploadService.uploadImage(file);
            return ApiResponse.success(imageUrl, "图片上传成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/image")
    public ApiResponse<Object> deleteImage(@RequestParam("url") String imageUrl) {
        try {
            boolean deleted = fileUploadService.deleteImage(imageUrl);
            if (deleted) {
                return ApiResponse.success("图片删除成功");
            } else {
                return ApiResponse.error("图片删除失败，文件可能不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("图片删除失败: " + e.getMessage());
        }
    }
}

