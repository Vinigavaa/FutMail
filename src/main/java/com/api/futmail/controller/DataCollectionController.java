package com.api.futmail.controller;

// Imports Spring
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Imports Java Standard
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Import do seu projeto
import com.api.futmail.service.NewsService;

@RestController
@RequestMapping("/api/data-collection")
@CrossOrigin(origins = "http://localhost:3000")
public class DataCollectionController {

    private final NewsService newsService;

    public DataCollectionController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping("/collect-news")
    public ResponseEntity<Map<String, Object>> collectNews() {
        NewsService.CollectionResult result = newsService.collectTodaysNews();

        Map<String, Object> response = new HashMap<>();
        response.put("created", result.getCreated());
        response.put("duplicates", result.getDuplicates());
        response.put("total_processed", result.getTotal());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}