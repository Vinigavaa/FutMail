package com.api.futmail.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.api.futmail.model.Subscriber;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberResponse {
    
    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private String status;
    private Boolean active;
    
    public static SubscriberResponse fromEntity(Subscriber subscriber) {
        if (subscriber == null) {
            return null;
        }
        
        return SubscriberResponse.builder()
                .id(subscriber.getId())
                .email(subscriber.getEmail())
                .createdAt(subscriber.getCreatedAt())
                .status(subscriber.getStatus().getDisplayName())
                .active(subscriber.getActive())
                .build();
    }
    
    public boolean isActiveSubscriber() {
        return Boolean.TRUE.equals(active) && "Ativo".equals(status);
    }
    
    public boolean canReceiveEmails() {
        return isActiveSubscriber();
    }
    
    public String getDisplayName() {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.substring(0, email.indexOf('@'));
    }
    
    public String getMaskedEmail() {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 3) {
            return email;
        }
        
        return username.substring(0, 2) + "***" + username.substring(username.length() - 1) + "@" + domain;
    }
}
