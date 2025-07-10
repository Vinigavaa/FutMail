package com.api.futmail.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.futmail.dto.NewsRequest;
import com.api.futmail.dto.NewsResponse;
import com.api.futmail.model.NewsCategory;
import com.api.futmail.service.NewsService;

import jakarta.validation.Valid;

// controller/NewsController.java
@RestController
@RequestMapping("/api/news")
public class NewsController {
    
    private final NewsService newsService;
    
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }
    
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody NewsRequest request) {
        try {
            NewsResponse response = newsService.createNews(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NewsResponse> news = newsService.getAllNews(page, size);
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<NewsResponse>> getTodaysNews() {
        List<NewsResponse> news = newsService.getTodaysNews();
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<NewsResponse>> getNewsByCategory(@PathVariable NewsCategory category) {
        List<NewsResponse> news = newsService.getNewsByCategory(category);
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<NewsResponse>> getRecentNews(
            @RequestParam(defaultValue = "1") int days) {
        List<NewsResponse> news = newsService.getRecentNews(days);
        return ResponseEntity.ok(news);
    }
}