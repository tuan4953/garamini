package com.garage.news.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {

    private Long id;

    @NotBlank(message = "Tiêu đề bài viết không được để trống")
    private String title;

    private String summary;

    @NotBlank(message = "Nội dung bài viết không được để trống")
    private String content;

    private String imageUrl;

    private Boolean published = true;
}