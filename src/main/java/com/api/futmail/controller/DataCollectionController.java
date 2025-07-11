package com.api.futmail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.futmail.model.CollectionResult;
import com.api.futmail.service.NewsService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/data-collection")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DataCollectionController {
    
    private final NewsService newsService;
    
    @PostMapping("/collect-news")
    public ResponseEntity<Map<String, Object>> collectNews() {
        try {
            log.info("🔄 Iniciando coleta automática de notícias");
            
            CollectionResult result = newsService.collectTodaysNews();
            
            Map<String, Object> response = buildCollectionResponse(result);
            
            log.info("✅ Coleta concluída: {}", result.getSummary());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Erro durante coleta de notícias: {}", e.getMessage());
            
            Map<String, Object> errorResponse = buildErrorResponse(e);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCollectionStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("service", "Data Collection Service");
            status.put("status", "ACTIVE");
            status.put("timestamp", LocalDateTime.now());
            status.put("description", "Serviço de coleta automática de notícias de futebol");
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("❌ Erro ao verificar status: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/collect-test")
    public ResponseEntity<Map<String, Object>> collectTestData() {
        try {
            log.info("🧪 Iniciando coleta de teste");
            
            // Aqui poderia ser implementado um método de teste
            Map<String, Object> testResponse = new HashMap<>();
            testResponse.put("message", "Coleta de teste não implementada");
            testResponse.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(testResponse);
        } catch (Exception e) {
            log.error("❌ Erro durante coleta de teste: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private Map<String, Object> buildCollectionResponse(CollectionResult result) {
        Map<String, Object> response = new HashMap<>();
        response.put("created", result.getCreated());
        response.put("duplicates", result.getDuplicates());
        response.put("total_processed", result.getTotal());
        response.put("creation_rate", result.getFormattedCreationRate());
        response.put("summary", result.getSummary());
        response.put("timestamp", LocalDateTime.now());
        response.put("success", true);
        
        return response;
    }
    
    private Map<String, Object> buildErrorResponse(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("created", 0);
        errorResponse.put("duplicates", 0);
        errorResponse.put("total_processed", 0);
        
        return errorResponse;
    }
}