package com.mathpractice.dto;

import lombok.Data;

import java.util.List;

/**
 * 选择题创建请求DTO
 */
@Data
public class ChoiceQuestionRequest {
    /**
     * 题目类型ID（1-单选题，2-多选题）
     */
    private Integer typeId;

    /**
     * 难度ID
     */
    private Integer difficultyId;

    /**
     * 科目
     */
    private String subject;

    /**
     * 知识点
     */
    private String knowledgePoint;

    /**
     * 题目内容（支持HTML，可包含图片）
     */
    private String content;

    /**
     * 题目解析
     */
    private String analysis;

    /**
     * 创建者ID
     */
    private Integer createdBy;

    /**
     * 选项列表
     */
    private List<OptionItem> options;

    /**
     * 正确答案（单选题为单个选项键，如"A"；多选题为多个选项键，如["A","B"]）
     */
    private List<String> correctAnswers;

    /**
     * 题目图片列表（图片路径列表）
     */
    private List<ImageItem> images;

    /**
     * 选项项
     */
    @Data
    public static class OptionItem {
        /**
         * 选项键（A, B, C, D等）
         */
        private String optionKey;

        /**
         * 选项内容（支持HTML，可包含图片）
         */
        private String content;

        /**
         * 排序
         */
        private Integer sortOrder;
    }

    /**
     * 图片项
     */
    @Data
    public static class ImageItem {
        /**
         * 图片路径（相对路径，如 /api/images/2025-11-21/image.jpg）
         */
        private String imagePath;

        /**
         * 图片文件名
         */
        private String imageName;
    }
}

