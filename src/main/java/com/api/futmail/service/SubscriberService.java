package com.api.futmail.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.api.futmail.dto.SubscriberRequest;
import com.api.futmail.dto.SubscriberResponse;
import com.api.futmail.model.Subscriber;
import com.api.futmail.model.SubscriptionStatus;
import com.api.futmail.repository.SubscriberRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SubscriberService {
    
    private final SubscriberRepository subscriberRepository;
    
    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }
    
    public SubscriberResponse subscribe(SubscriberRequest request) {
        // Verificar se email já existe
        if (subscriberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(request.getEmail());
        subscriber.setStatus(SubscriptionStatus.ACTIVE);
        
        Subscriber saved = subscriberRepository.save(subscriber);
        
        return new SubscriberResponse(
            saved.getId(),
            saved.getEmail(),
            saved.getCreatedAt(),
            saved.getStatus().name()
        );
    }
    
    public List<SubscriberResponse> getActiveSubscribers() {
        return subscriberRepository.findByStatus(SubscriptionStatus.ACTIVE)
            .stream()
            .map(sub -> new SubscriberResponse(
                sub.getId(),
                sub.getEmail(),
                sub.getCreatedAt(),
                sub.getStatus().name()
            ))
            .collect(Collectors.toList());
    }
}
