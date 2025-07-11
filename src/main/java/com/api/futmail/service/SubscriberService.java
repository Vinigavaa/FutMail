package com.api.futmail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.futmail.dto.SubscriberRequest;
import com.api.futmail.dto.SubscriberResponse;
import com.api.futmail.model.Subscriber;
import com.api.futmail.model.SubscriptionStatus;
import com.api.futmail.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriberService {
    
    private final SubscriberRepository subscriberRepository;
    
    public SubscriberResponse subscribe(SubscriberRequest request) {
        validateSubscriberRequest(request);
        
        if (isEmailAlreadyRegistered(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        Subscriber subscriber = createSubscriberFromRequest(request);
        Subscriber savedSubscriber = subscriberRepository.save(subscriber);
        
        log.info("✅ Novo assinante cadastrado: {}", savedSubscriber.getEmail());
        
        return SubscriberResponse.fromEntity(savedSubscriber);
    }
    
    public List<SubscriberResponse> getActiveSubscribers() {
        return subscriberRepository.findByStatus(SubscriptionStatus.ACTIVE)
                .stream()
                .map(SubscriberResponse::fromEntity)
                .toList();
    }
    
    public List<SubscriberResponse> getAllSubscribers() {
        return subscriberRepository.findAll()
                .stream()
                .map(SubscriberResponse::fromEntity)
                .toList();
    }
    
    public Optional<SubscriberResponse> findByEmail(String email) {
        return subscriberRepository.findByEmail(email)
                .map(SubscriberResponse::fromEntity);
    }
    
    public SubscriberResponse unsubscribe(String email) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email não encontrado"));
        
        subscriber.unsubscribe();
        Subscriber updatedSubscriber = subscriberRepository.save(subscriber);
        
        log.info("📧 Assinante descadastrado: {}", email);
        
        return SubscriberResponse.fromEntity(updatedSubscriber);
    }
    
    public SubscriberResponse reactivateSubscriber(String email) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email não encontrado"));
        
        subscriber.activate();
        Subscriber updatedSubscriber = subscriberRepository.save(subscriber);
        
        log.info("🔄 Assinante reativado: {}", email);
        
        return SubscriberResponse.fromEntity(updatedSubscriber);
    }
    
    public long countActiveSubscribers() {
        return subscriberRepository.findByStatus(SubscriptionStatus.ACTIVE).size();
    }
    
    public long countTotalSubscribers() {
        return subscriberRepository.count();
    }
    
    private void validateSubscriberRequest(SubscriberRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email deve ser válido");
        }
    }
    
    private boolean isEmailAlreadyRegistered(String email) {
        return subscriberRepository.existsByEmail(email);
    }
    
    private Subscriber createSubscriberFromRequest(SubscriberRequest request) {
        return Subscriber.builder()
                .email(request.getEmail().trim().toLowerCase())
                .build();
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
