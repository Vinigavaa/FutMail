package com.api.futmail.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.api.futmail.model.Newsletter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterResponse {
    
    private Long id;
    private String subject;
    private String content;
    private String htmlContent;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private String status;
    private Integer totalSubscribers;
    private Integer emailsSent;
    private Integer emailsFailed;
    private Double successRate;
    
    public static NewsletterResponse fromEntity(Newsletter newsletter) {
        if (newsletter == null) {
            return null;
        }
        
        return NewsletterResponse.builder()
                .id(newsletter.getId())
                .subject(newsletter.getSubject())
                .content(newsletter.getContent())
                .htmlContent(newsletter.getHtmlContent())
                .sentAt(newsletter.getSentAt())
                .createdAt(newsletter.getCreatedAt())
                .status(newsletter.getStatus().getDisplayName())
                .totalSubscribers(newsletter.getTotalSubscribers())
                .emailsSent(newsletter.getEmailsSent())
                .emailsFailed(newsletter.getEmailsFailed())
                .successRate(newsletter.getSuccessRate())
                .build();
    }
    
    public boolean hasBeenSent() {
        return sentAt != null;
    }
    
    public boolean isSuccessful() {
        return hasBeenSent() && emailsFailed != null && emailsFailed == 0;
    }
    
    public boolean hasFailures() {
        return emailsFailed != null && emailsFailed > 0;
    }
    
    public String getFormattedSuccessRate() {
        if (successRate == null) {
            return "N/A";
        }
        return String.format("%.1f%%", successRate);
    }
    
    public String getShortContent() {
        if (content == null || content.length() <= 200) {
            return content;
        }
        return content.substring(0, 200) + "...";
    }
}
