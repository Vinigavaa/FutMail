package com.api.futmail.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.api.futmail.model.News;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Boolean active;
    
    public static NewsResponse fromEntity(News news) {
        if (news == null) {
            return null;
        }
        
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .summary(news.getSummary())
                .category(news.getCategory().getDisplayName())
                .sourceUrl(news.getSourceUrl())
                .sourceName(news.getSourceName())
                .publishedAt(news.getPublishedAt())
                .createdAt(news.getCreatedAt())
                .active(news.getActive())
                .build();
    }
    
    public boolean isPublishedToday() {
        return publishedAt != null && 
               publishedAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    public boolean hasSource() {
        return sourceName != null && !sourceName.trim().isEmpty();
    }
    
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
    
    public String getShortSummary() {
        if (summary == null || summary.length() <= 100) {
            return summary;
        }
        return summary.substring(0, 100) + "...";
    }
}
