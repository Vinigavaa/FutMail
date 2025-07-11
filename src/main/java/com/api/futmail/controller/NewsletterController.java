package com.api.futmail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.futmail.dto.NewsletterResponse;
import com.api.futmail.service.NewsletterService;

@Slf4j
@RestController
@RequestMapping("/api/newsletters")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NewsletterController {
    
    private static final int DEFAULT_PAGE_SIZE = 10;
    
    private final NewsletterService newsletterService;
    
    @PostMapping("/create-daily")
    public ResponseEntity<NewsletterResponse> createDailyNewsletter() {
        try {
            log.info("📰 Solicitação para criar newsletter diária");
            NewsletterResponse response = newsletterService.createDailyNewsletter();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Erro ao criar newsletter diária: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/{id}/send")
    public ResponseEntity<NewsletterResponse> sendNewsletter(@PathVariable Long id) {
        try {
            log.info("📤 Solicitação para enviar newsletter ID: {}", id);
            NewsletterResponse response = newsletterService.sendNewsletter(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("❌ Erro ao enviar newsletter: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao enviar newsletter: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<NewsletterResponse>> getAllNewsletters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (page < 0 || size <= 0) {
            log.warn("❌ Parâmetros inválidos: page={}, size={}", page, size);
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Page<NewsletterResponse> newsletters = newsletterService.getAllNewsletters(page, size);
            log.info("📋 Retornando {} newsletters (página {})", newsletters.getContent().size(), page);
            return ResponseEntity.ok(newsletters);
        } catch (Exception e) {
            log.error("❌ Erro ao buscar newsletters: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NewsletterResponse> getNewsletterById(@PathVariable Long id) {
        try {
            // Aqui seria implementado um método no service para buscar por ID
            log.info("🔍 Buscando newsletter ID: {}", id);
            return ResponseEntity.ok().build(); // Placeholder
        } catch (Exception e) {
            log.error("❌ Erro ao buscar newsletter: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<NewsletterStats> getNewsletterStats() {
        try {
            // Aqui seria implementado um método de estatísticas
            log.info("📊 Buscando estatísticas de newsletters");
            return ResponseEntity.ok().build(); // Placeholder
        } catch (Exception e) {
            log.error("❌ Erro ao buscar estatísticas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // DTO interno para estatísticas
    public static class NewsletterStats {
        private final long totalNewsletters;
        private final long sentNewsletters;
        private final long failedNewsletters;
        private final double successRate;
        
        public NewsletterStats(long totalNewsletters, long sentNewsletters, long failedNewsletters) {
            this.totalNewsletters = totalNewsletters;
            this.sentNewsletters = sentNewsletters;
            this.failedNewsletters = failedNewsletters;
            this.successRate = totalNewsletters == 0 ? 0.0 : (double) sentNewsletters / totalNewsletters * 100;
        }
        
        public long getTotalNewsletters() { return totalNewsletters; }
        public long getSentNewsletters() { return sentNewsletters; }
        public long getFailedNewsletters() { return failedNewsletters; }
        public double getSuccessRate() { return successRate; }
    }
}
