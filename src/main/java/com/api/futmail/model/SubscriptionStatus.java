package com.api.futmail.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    ACTIVE("Ativo"),
    INACTIVE("Inativo"),
    UNSUBSCRIBED("Descadastrado");
    
    private final String displayName;
    
    public boolean canReceiveEmails() {
        return this == ACTIVE;
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    public boolean isUnsubscribed() {
        return this == UNSUBSCRIBED;
    }
}
