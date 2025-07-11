package com.api.futmail.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscribers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "email")
public class Subscriber {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.active = true;
        this.status = SubscriptionStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.active = false;
        this.status = SubscriptionStatus.INACTIVE;
    }
    
    public void unsubscribe() {
        this.active = false;
        this.status = SubscriptionStatus.UNSUBSCRIBED;
    }
    
    public boolean isActiveSubscriber() {
        return Boolean.TRUE.equals(active) && status == SubscriptionStatus.ACTIVE;
    }
    
    public boolean isUnsubscribed() {
        return status == SubscriptionStatus.UNSUBSCRIBED;
    }
    
    public boolean canReceiveEmails() {
        return isActiveSubscriber();
    }
    
    public String getDisplayName() {
        return email.substring(0, email.indexOf('@'));
    }
}

