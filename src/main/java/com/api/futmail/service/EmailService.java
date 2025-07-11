package com.api.futmail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.api.futmail.model.EmailSendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class EmailService {
    
    private static final double SUCCESS_RATE_THRESHOLD = 0.95;
    private static final int EMAIL_SEND_DELAY_MS = 100;
    private static final int MAX_CONTENT_PREVIEW_LENGTH = 100;
    
    @Value("${app.email.from:newsletter@futmail.com}")
    private String fromEmail;
    
    @Value("${app.email.from-name:Futmail Newsletter}")
    private String fromName;
    
    public boolean sendEmail(String to, String subject, String htmlContent) {
        try {
            logEmailSending(to, subject, htmlContent);
            simulateDelay();
            return simulateSuccess();
            
        } catch (Exception e) {
            log.error("❌ Erro ao enviar email para {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    public EmailSendResult sendBulkEmails(List<String> emails, String subject, String htmlContent) {
        log.info("🚀 Iniciando envio em massa para {} emails", emails.size());
        
        if (emails.isEmpty()) {
            log.warn("⚠️ Lista de emails vazia");
            return EmailSendResult.of(0, 0);
        }
        
        AtomicInteger sent = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);
        
        List<CompletableFuture<Boolean>> futures = createEmailFutures(emails, subject, htmlContent);
        processEmailFutures(futures, sent, failed);
        
        EmailSendResult result = EmailSendResult.of(sent.get(), failed.get());
        logBulkEmailResult(result);
        
        return result;
    }
    
    private void logEmailSending(String to, String subject, String htmlContent) {
        log.info("📧 Simulando envio de email para: {}", to);
        log.debug("Assunto: {}", subject);
        log.debug("Conteúdo: {}", truncateContent(htmlContent, MAX_CONTENT_PREVIEW_LENGTH));
    }
    
    private void simulateDelay() throws InterruptedException {
        Thread.sleep(EMAIL_SEND_DELAY_MS);
    }
    
    private boolean simulateSuccess() {
        return Math.random() > (1 - SUCCESS_RATE_THRESHOLD);
    }
    
    private List<CompletableFuture<Boolean>> createEmailFutures(List<String> emails, String subject, String htmlContent) {
        return emails.stream()
                .map(email -> CompletableFuture.supplyAsync(() -> 
                    sendEmail(email, subject, htmlContent)))
                .toList();
    }
    
    private void processEmailFutures(List<CompletableFuture<Boolean>> futures, AtomicInteger sent, AtomicInteger failed) {
        futures.forEach(future -> {
            try {
                if (future.get()) {
                    sent.incrementAndGet();
                } else {
                    failed.incrementAndGet();
                }
            } catch (Exception e) {
                failed.incrementAndGet();
                log.error("❌ Erro no envio assíncrono: {}", e.getMessage());
            }
        });
    }
    
    private void logBulkEmailResult(EmailSendResult result) {
        log.info("✅ Envio concluído: {}", result.getSummary());
    }
    
    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
