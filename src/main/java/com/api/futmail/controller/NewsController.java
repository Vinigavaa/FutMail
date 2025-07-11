package com.api.futmail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.futmail.dto.NewsRequest;
import com.api.futmail.dto.NewsResponse;
import com.api.futmail.model.NewsCategory;
import com.api.futmail.service.NewsService;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NewsController {
    
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_RECENT_DAYS = 1;
    
    private final NewsService newsService;
    
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody NewsRequest request) {
        try {
            log.info("📝 Criando nova notícia: {}", request.getTitle());
            NewsResponse response = newsService.createNews(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("❌ Erro ao criar notícia: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao criar notícia: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<NewsResponse> news = newsService.getAllNews(page, size);
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<NewsResponse>> getTodaysNews() {
        List<NewsResponse> news = newsService.getTodaysNews();
        log.info("📰 Retornando {} notícias de hoje", news.size());
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<NewsResponse>> getNewsByCategory(@PathVariable NewsCategory category) {
        try {
            List<NewsResponse> news = newsService.getNewsByCategory(category);
            log.info("📂 Retornando {} notícias da categoria: {}", news.size(), category.getDisplayName());
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            log.error("❌ Erro ao buscar notícias por categoria: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<NewsResponse>> getRecentNews(
            @RequestParam(defaultValue = "1") int days) {
        
        if (days <= 0 || days > 30) {
            return ResponseEntity.badRequest().build();
        }
        
        List<NewsResponse> news = newsService.getRecentNews(days);
        log.info("📅 Retornando {} notícias dos últimos {} dias", news.size(), days);
        return ResponseEntity.ok(news);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getNewsStats() {
        try {
            // Aqui poderia ser implementado um método de estatísticas
            return ResponseEntity.ok("Estatísticas não implementadas ainda");
        } catch (Exception e) {
            log.error("❌ Erro ao buscar estatísticas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}