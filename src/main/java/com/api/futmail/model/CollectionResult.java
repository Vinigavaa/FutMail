package com.api.futmail.model;

import lombok.*;

@Value
@Builder
public class CollectionResult {
    
    int created;
    int duplicates;
    int total;
    double creationRate;
    
    public static CollectionResult of(int created, int duplicates) {
        int total = created + duplicates;
        double creationRate = total == 0 ? 0.0 : (double) created / total * 100;
        
        return CollectionResult.builder()
                .created(created)
                .duplicates(duplicates)
                .total(total)
                .creationRate(creationRate)
                .build();
    }
    
    public boolean hasNewContent() {
        return created > 0;
    }
    
    public boolean hasOnlyDuplicates() {
        return total > 0 && created == 0;
    }
    
    public boolean hasNoContent() {
        return total == 0;
    }
    
    public String getFormattedCreationRate() {
        return String.format("%.1f%%", creationRate);
    }
    
    public String getSummary() {
        if (hasNoContent()) {
            return "Nenhum conteúdo processado";
        }
        
        if (hasOnlyDuplicates()) {
            return String.format("Todos os %d itens eram duplicados", duplicates);
        }
        
        if (hasNewContent()) {
            return String.format("%d novos itens criados, %d duplicados (%s novos)", 
                    created, duplicates, getFormattedCreationRate());
        }
        
        return String.format("%d itens processados", total);
    }
} 