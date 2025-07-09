package com.api.futmail.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.futmail.dto.SubscriberRequest;
import com.api.futmail.dto.SubscriberResponse;
import com.api.futmail.service.SubscriberService;

import jakarta.validation.Valid;

// controller/SubscriberController.java
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {
    
    private final SubscriberService subscriberService;
    
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }
    
    @PostMapping
    public ResponseEntity<SubscriberResponse> subscribe(
            @Valid @RequestBody SubscriberRequest request) {
        try {
            SubscriberResponse response = subscriberService.subscribe(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<SubscriberResponse>> getSubscribers() {
        List<SubscriberResponse> subscribers = subscriberService.getActiveSubscribers();
        return ResponseEntity.ok(subscribers);
    }
}