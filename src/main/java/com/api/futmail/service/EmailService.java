package com.api.futmail.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${app.email.from:newsletter@futmail.com}")
    private String fromEmail;
    
    @Value("${app.email.from-name:Futmail Newsletter}")
    private String fromName;
    
    public boolean sendEmail(String to, String subject, String htmlContent) {
        try {
            // Por enquanto, vamos simular o envio
            logger.info("📧 Simulando envio de email:");
            logger.info("Para: {}", to);
            logger.info("Assunto: {}", subject);
            logger.info("Conteúdo: {}", htmlContent.substring(0, Math.min(100, htmlContent.length())) + "...");
            
            // Simular delay de envio
            Thread.sleep(100);
            
            // Simular 95% de sucesso
            return Math.random() > 0.05;
            
        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    public EmailSendResult sendBulkEmails(List<String> emails, String subject, String htmlContent) {
        int sent = 0;
        int failed = 0;
        
        logger.info("🚀 Iniciando envio em massa para {} emails", emails.size());
        
        for (String email : emails) {
            if (sendEmail(email, subject, htmlContent)) {
                sent++;
            } else {
                failed++;
            }
        }
        
        logger.info("✅ Envio concluído: {} enviados, {} falharam", sent, failed);
        
        return new EmailSendResult(sent, failed);
    }
    
    // Classe interna para resultado
    public static class EmailSendResult {
        private final int sent;
        private final int failed;
        
        public EmailSendResult(int sent, int failed) {
            this.sent = sent;
            this.failed = failed;
        }
        
        public int getSent() { return sent; }
        public int getFailed() { return failed; }
        public int getTotal() { return sent + failed; }
    }
}
