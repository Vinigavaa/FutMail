package com.api.futmail.model;

public enum NewsletterStatus {
    DRAFT("Rascunho"),
    SENDING("Enviando"),
    SENT("Enviada"),
    FAILED("Falhou");
    
    private final String displayName;
    
    NewsletterStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
