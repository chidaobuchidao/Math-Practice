package com.mathpractice.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 创建试卷请求参数
 */
@Getter
@Setter
public class GeneratePaperRequest {
    private Integer studentId;
    private String title;
    private List<Integer> questionIds;
}
