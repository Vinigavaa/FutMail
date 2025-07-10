package com.api.futmail.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.futmail.dto.NewsletterResponse;
import com.api.futmail.service.NewsletterService;

// controller/NewsletterController.java
@RestController
@RequestMapping("/api/newsletters")
public class NewsletterController {
    
    private final NewsletterService newsletterService;
    
    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }
    
    @PostMapping("/create-daily")
    public ResponseEntity<NewsletterResponse> createDailyNewsletter() {
        NewsletterResponse response = newsletterService.createDailyNewsletter();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/send")
    public ResponseEntity<NewsletterResponse> sendNewsletter(@PathVariable Long id) {
        try {
            NewsletterResponse response = newsletterService.sendNewsletter(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<NewsletterResponse>> getAllNewsletters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NewsletterResponse> newsletters = newsletterService.getAllNewsletters(page, size);
        return ResponseEntity.ok(newsletters);
    }
}
