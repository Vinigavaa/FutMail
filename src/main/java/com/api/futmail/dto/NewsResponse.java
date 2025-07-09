package com.api.futmail.dto;

import java.time.LocalDateTime;

import com.api.futmail.model.News;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsResponse {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String category;
    private String sourceUrl;
    private String sourceName;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    
    public static NewsResponse fromEntity(News news) {
        NewsResponse response = new NewsResponse();
        response.setId(news.getId());
        response.setTitle(news.getTitle());
        response.setContent(news.getContent());
        response.setSummary(news.getSummary());
        response.setCategory(news.getCategory().getDisplayName());
        response.setSourceUrl(news.getSourceUrl());
        response.setSourceName(news.getSourceName());
        response.setPublishedAt(news.getPublishedAt());
        response.setCreatedAt(news.getCreatedAt());
        return response;
    }
}
