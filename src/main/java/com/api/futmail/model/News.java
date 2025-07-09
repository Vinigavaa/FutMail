package com.api.futmail.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "news")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(length = 1000)
    private String summary; // Resumo de 1-2 frases
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsCategory category;
    
    @Column(name = "source_url")
    private String sourceUrl;
    
    @Column(name = "source_name")
    private String sourceName;
    
    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    // Para evitar notícias duplicadas
    @Column(unique = true)
    private String contentHash;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (contentHash == null && content != null) {
            contentHash = generateContentHash();
        }
    }

     private String generateContentHash() {
        return Integer.toHexString((title + content).hashCode());
    }
}
