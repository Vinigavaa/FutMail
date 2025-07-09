package com.api.futmail.model;

public enum NewsCategory {
    TRANSFERS("Transferências"),
    RESULTS("Resultados"),
    RUMORS("Rumores"),
    GENERAL("Geral"),
    BRAZILIAN_LEAGUE("Brasileirão"),
    INTERNATIONAL("Internacional");
    
    private final String displayName;
    
    NewsCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
