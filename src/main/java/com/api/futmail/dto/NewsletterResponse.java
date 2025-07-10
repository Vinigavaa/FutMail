package com.api.futmail.dto;

import java.time.LocalDateTime;

import com.api.futmail.model.Newsletter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    
    public static NewsletterResponse fromEntity(Newsletter newsletter) {
        NewsletterResponse response = new NewsletterResponse();
        response.setId(newsletter.getId());
        response.setSubject(newsletter.getSubject());
        response.setContent(newsletter.getContent());
        response.setHtmlContent(newsletter.getHtmlContent());
        response.setSentAt(newsletter.getSentAt());
        response.setCreatedAt(newsletter.getCreatedAt());
        response.setStatus(newsletter.getStatus().getDisplayName());
        response.setTotalSubscribers(newsletter.getTotalSubscribers());
        response.setEmailsSent(newsletter.getEmailsSent());
        response.setEmailsFailed(newsletter.getEmailsFailed());
        return response;
    }
}
