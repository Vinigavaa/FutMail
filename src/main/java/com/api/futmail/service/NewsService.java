package com.api.futmail.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.api.futmail.dto.NewsRequest;
import com.api.futmail.dto.NewsResponse;
import com.api.futmail.model.News;
import com.api.futmail.model.NewsCategory;
import com.api.futmail.repository.NewsRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NewsService {
    
    private final NewsRepository newsRepository;
    
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    
    public NewsResponse createNews(NewsRequest request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setSummary(request.getSummary());
        news.setCategory(request.getCategory());
        news.setSourceUrl(request.getSourceUrl());
        news.setSourceName(request.getSourceName());
        news.setPublishedAt(LocalDateTime.now());
        
        // Verificar duplicata
        String contentHash = Integer.toHexString(
            (request.getTitle() + request.getContent()).hashCode()
        );
        
        if (newsRepository.existsByContentHash(contentHash)) {
            throw new IllegalArgumentException("Notícia já existe");
        }
        
        news.setContentHash(contentHash);
        News saved = newsRepository.save(news);
        
        return NewsResponse.fromEntity(saved);
    }
    
    public List<NewsResponse> getTodaysNews() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        
        return newsRepository.findTodaysNews(startOfDay, endOfDay)
            .stream()
            .map(NewsResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    public List<NewsResponse> getNewsByCategory(NewsCategory category) {
        return newsRepository.findByCategory(category)
            .stream()
            .map(NewsResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    public Page<NewsResponse> getAllNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.findActiveNews(pageable)
            .map(NewsResponse::fromEntity);
    }
    
    public List<NewsResponse> getRecentNews(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return newsRepository.findActiveNewsAfterDate(since)
            .stream()
            .map(NewsResponse::fromEntity)
            .collect(Collectors.toList());
    }
}