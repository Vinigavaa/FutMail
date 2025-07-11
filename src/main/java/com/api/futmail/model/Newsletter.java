package com.api.futmail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "newsletters")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Newsletter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String subject;
    
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsletterStatus status = NewsletterStatus.DRAFT;
    
    @Builder.Default
    @Column(name = "total_subscribers")
    private Integer totalSubscribers = 0;
    
    @Builder.Default
    @Column(name = "emails_sent")
    private Integer emailsSent = 0;
    
    @Builder.Default
    @Column(name = "emails_failed")
    private Integer emailsFailed = 0;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void markAsSent(int totalSubscribers, int emailsSent, int emailsFailed) {
        this.totalSubscribers = totalSubscribers;
        this.emailsSent = emailsSent;
        this.emailsFailed = emailsFailed;
        this.sentAt = LocalDateTime.now();
        this.status = emailsFailed == 0 ? NewsletterStatus.SENT : NewsletterStatus.FAILED;
    }
    
    public void markAsStartedSending() {
        this.status = NewsletterStatus.SENDING;
    }
    
    public boolean canBeSent() {
        return status == NewsletterStatus.DRAFT;
    }
    
    public boolean isAlreadySent() {
        return status == NewsletterStatus.SENT;
    }
    
    public boolean hasFailed() {
        return status == NewsletterStatus.FAILED;
    }
    
    public double getSuccessRate() {
        if (totalSubscribers == null || totalSubscribers == 0) {
            return 0.0;
        }
        return (double) emailsSent / totalSubscribers * 100;
    }
    
    public boolean isSuccessful() {
        return isAlreadySent() && emailsFailed == 0;
    }
}
