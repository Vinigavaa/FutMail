package com.api.futmail.model;

import lombok.*;

@Value
@Builder
public class EmailSendResult {
    
    int sent;
    int failed;
    int total;
    double successRate;
    
    public static EmailSendResult of(int sent, int failed) {
        int total = sent + failed;
        double successRate = total == 0 ? 0.0 : (double) sent / total * 100;
        
        return EmailSendResult.builder()
                .sent(sent)
                .failed(failed)
                .total(total)
                .successRate(successRate)
                .build();
    }
    
    public boolean isFullSuccess() {
        return failed == 0 && sent > 0;
    }
    
    public boolean hasFailures() {
        return failed > 0;
    }
    
    public boolean hasNoEmails() {
        return total == 0;
    }
    
    public String getFormattedSuccessRate() {
        return String.format("%.1f%%", successRate);
    }
    
    public String getSummary() {
        if (hasNoEmails()) {
            return "Nenhum email processado";
        }
        
        if (isFullSuccess()) {
            return String.format("Todos os %d emails foram enviados com sucesso", sent);
        }
        
        if (hasFailures()) {
            return String.format("%d emails enviados, %d falharam (%s sucesso)", 
                    sent, failed, getFormattedSuccessRate());
        }
        
        return String.format("%d emails processados", total);
    }
} 