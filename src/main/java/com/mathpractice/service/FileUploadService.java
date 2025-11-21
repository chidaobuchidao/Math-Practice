package com.mathpractice.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    /**
     * 上传图片文件
     * @param file 上传的文件
     * @return 文件的访问URL
     */
    String uploadImage(MultipartFile file);

    /**
     * 删除图片文件
     * @param imageUrl 图片URL
     * @return 是否删除成功
     */
    boolean deleteImage(String imageUrl);
}

