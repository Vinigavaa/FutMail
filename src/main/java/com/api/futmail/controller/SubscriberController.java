package com.api.futmail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.futmail.dto.SubscriberRequest;
import com.api.futmail.dto.SubscriberResponse;
import com.api.futmail.service.SubscriberService;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SubscriberController {
    
    private final SubscriberService subscriberService;
    
    @PostMapping
    public ResponseEntity<SubscriberResponse> subscribe(@Valid @RequestBody SubscriberRequest request) {
        try {
            log.info("📧 Novo cadastro de assinante: {}", request.getEmail());
            SubscriberResponse response = subscriberService.subscribe(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("❌ Erro ao cadastrar assinante: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao cadastrar assinante: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<SubscriberResponse>> getSubscribers() {
        try {
            List<SubscriberResponse> subscribers = subscriberService.getActiveSubscribers();
            log.info("📋 Retornando {} assinantes ativos", subscribers.size());
            return ResponseEntity.ok(subscribers);
        } catch (Exception e) {
            log.error("❌ Erro ao buscar assinantes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<SubscriberResponse>> getAllSubscribers() {
        try {
            List<SubscriberResponse> subscribers = subscriberService.getAllSubscribers();
            log.info("📋 Retornando {} assinantes (todos)", subscribers.size());
            return ResponseEntity.ok(subscribers);
        } catch (Exception e) {
            log.error("❌ Erro ao buscar todos os assinantes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/unsubscribe")
    public ResponseEntity<SubscriberResponse> unsubscribe(@RequestBody EmailRequest request) {
        try {
            log.info("📧 Descadastro de assinante: {}", request.getEmail());
            SubscriberResponse response = subscriberService.unsubscribe(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("❌ Erro ao descadastrar assinante: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao descadastrar assinante: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/reactivate")
    public ResponseEntity<SubscriberResponse> reactivate(@RequestBody EmailRequest request) {
        try {
            log.info("🔄 Reativação de assinante: {}", request.getEmail());
            SubscriberResponse response = subscriberService.reactivateSubscriber(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("❌ Erro ao reativar assinante: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erro interno ao reativar assinante: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<SubscriberStats> getSubscriberStats() {
        try {
            long activeCount = subscriberService.countActiveSubscribers();
            long totalCount = subscriberService.countTotalSubscribers();
            
            SubscriberStats stats = new SubscriberStats(activeCount, totalCount);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("❌ Erro ao buscar estatísticas de assinantes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // DTOs internos
    public static class EmailRequest {
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class SubscriberStats {
        private final long activeSubscribers;
        private final long totalSubscribers;
        private final long inactiveSubscribers;
        
        public SubscriberStats(long activeSubscribers, long totalSubscribers) {
            this.activeSubscribers = activeSubscribers;
            this.totalSubscribers = totalSubscribers;
            this.inactiveSubscribers = totalSubscribers - activeSubscribers;
        }
        
        public long getActiveSubscribers() { return activeSubscribers; }
        public long getTotalSubscribers() { return totalSubscribers; }
        public long getInactiveSubscribers() { return inactiveSubscribers; }
        public double getActiveRate() { 
            return totalSubscribers == 0 ? 0.0 : (double) activeSubscribers / totalSubscribers * 100;
        }
    }
}