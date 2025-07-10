package com.api.futmail.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.api.futmail.dto.NewsResponse;
import com.api.futmail.dto.NewsletterResponse;
import com.api.futmail.dto.SubscriberResponse;
import com.api.futmail.model.Newsletter;
import com.api.futmail.model.NewsletterStatus;
import com.api.futmail.repository.NewsletterRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class NewsletterService {
    private final Logger logger = LoggerFactory.getLogger(NewsletterService.class);

    private final NewsletterRepository newsletterRepository;
    private final NewsService newsService;
    private final SubscriberService subscriberService;
    private final EmailService emailService;
    
    public NewsletterResponse createDailyNewsletter() {
        logger.info("📰 Criando newsletter diária");
        
        // Buscar notícias do dia
        List<NewsResponse> todaysNews = newsService.getTodaysNews();
        
        if (todaysNews.isEmpty()) {
            // Se não há notícias de hoje, pegar dos últimos 2 dias
            todaysNews = newsService.getRecentNews(2);
        }
        
        // Criar newsletter
        Newsletter newsletter = new Newsletter();
        newsletter.setSubject(generateSubject());
        newsletter.setContent(generateTextContent(todaysNews));
        newsletter.setHtmlContent(generateHtmlContent(todaysNews));
        newsletter.setStatus(NewsletterStatus.DRAFT);
        
        Newsletter saved = newsletterRepository.save(newsletter);
        logger.info("✅ Newsletter criada com ID: {}", saved.getId());
        
        return NewsletterResponse.fromEntity(saved);
    }
    
    public NewsletterResponse sendNewsletter(Long newsletterId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
            .orElseThrow(() -> new IllegalArgumentException("Newsletter não encontrada"));
        
        if (newsletter.getStatus() != NewsletterStatus.DRAFT) {
            throw new IllegalArgumentException("Newsletter já foi enviada ou está em processo de envio");
        }
        
        logger.info("📤 Iniciando envio da newsletter ID: {}", newsletterId);
        
        // Buscar assinantes ativos
        List<SubscriberResponse> subscribers = subscriberService.getActiveSubscribers();
        List<String> emails = subscribers.stream()
            .map(SubscriberResponse::getEmail)
            .collect(Collectors.toList());
        
        newsletter.setStatus(NewsletterStatus.SENDING);
        newsletter.setTotalSubscribers(emails.size());
        newsletterRepository.save(newsletter);
        
        // Enviar emails
        EmailService.EmailSendResult result = emailService.sendBulkEmails(
            emails, 
            newsletter.getSubject(), 
            newsletter.getHtmlContent()
        );
        
        // Atualizar status
        newsletter.setEmailsSent(result.getSent());
        newsletter.setEmailsFailed(result.getFailed());
        newsletter.setStatus(result.getFailed() == 0 ? NewsletterStatus.SENT : NewsletterStatus.FAILED);
        newsletter.setSentAt(LocalDateTime.now());
        
        Newsletter updated = newsletterRepository.save(newsletter);
        logger.info("✅ Newsletter enviada: {} sucessos, {} falhas", result.getSent(), result.getFailed());
        
        return NewsletterResponse.fromEntity(updated);
    }
    
    private String generateSubject() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return String.format("⚽ Futmail - %s | Sua dose diária de futebol", today.format(formatter));
    }
    
    private String generateTextContent(List<NewsResponse> news) {
        StringBuilder content = new StringBuilder();
        content.append("🏆 FUTMAIL - SUA NEWSLETTER DIÁRIA DE FUTEBOL\n\n");
        
        if (news.isEmpty()) {
            content.append("Nenhuma notícia disponível hoje.\n");
            return content.toString();
        }
        
        content.append("📰 PRINCIPAIS NOTÍCIAS DO DIA:\n\n");
        
        for (int i = 0; i < Math.min(5, news.size()); i++) {
            NewsResponse n = news.get(i);
            content.append(String.format("%d. %s\n", i + 1, n.getTitle()));
            if (n.getSummary() != null) {
                content.append(String.format("   %s\n", n.getSummary()));
            }
            content.append("\n");
        }
        
        content.append("Acesse nosso site para mais notícias!\n");
        content.append("\nObrigado por assinar o Futmail! ⚽");
        
        return content.toString();
    }
    
    private String generateHtmlContent(List<NewsResponse> news) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Futmail Newsletter</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; }
                    .header { background-color: #1e3a8a; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .news-item { margin-bottom: 20px; padding: 15px; border-left: 4px solid #1e3a8a; }
                    .news-title { font-weight: bold; font-size: 16px; margin-bottom: 8px; }
                    .news-summary { color: #666; line-height: 1.4; }
                    .footer { background-color: #f3f4f6; padding: 15px; text-align: center; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>⚽ FUTMAIL</h1>
                    <p>Sua dose diária de futebol em 5 minutos</p>
                </div>
                <div class="content">
            """);
        
        if (news.isEmpty()) {
            html.append("<p>Nenhuma notícia disponível hoje.</p>");
        } else {
            html.append("<h2>📰 Principais notícias do dia:</h2>");
            
            for (int i = 0; i < Math.min(5, news.size()); i++) {
                NewsResponse n = news.get(i);
                html.append("<div class='news-item'>");
                html.append("<div class='news-title'>").append(n.getTitle()).append("</div>");
                if (n.getSummary() != null) {
                    html.append("<div class='news-summary'>").append(n.getSummary()).append("</div>");
                }
                html.append("</div>");
            }
        }
        
        html.append("""
                </div>
                <div class="footer">
                    <p>Obrigado por assinar o Futmail! ⚽</p>
                    <p><a href="#">Descadastrar</a></p>
                </div>
            </body>
            </html>
            """);
        
        return html.toString();
    }
    
    public Page<NewsletterResponse> getAllNewsletters(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return newsletterRepository.findAllOrderByCreatedAtDesc(pageable)
            .map(NewsletterResponse::fromEntity);
    }
}
