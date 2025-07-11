package com.api.futmail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class News {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Size(max = 1000)
    @Column(length = 1000)
    private String summary;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsCategory category;
    
    @Column(name = "source_url")
    private String sourceUrl;
    
    @Column(name = "source_name")
    private String sourceName;
    
    @NotNull
    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(unique = true)
    private String contentHash;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (contentHash == null && hasContent()) {
            contentHash = generateContentHash();
        }
    }
    
    private boolean hasContent() {
        return title != null && content != null;
    }
    
    private String generateContentHash() {
        return Integer.toHexString((title + content).hashCode());
    }
    
    public boolean isPublishedToday() {
        return publishedAt != null && 
               publishedAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public void activate() {
        this.active = true;
    }
    
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
