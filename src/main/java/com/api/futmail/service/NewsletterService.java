package com.api.futmail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.futmail.dto.NewsResponse;
import com.api.futmail.dto.NewsletterResponse;
import com.api.futmail.dto.SubscriberResponse;
import com.api.futmail.model.EmailSendResult;
import com.api.futmail.model.Newsletter;
import com.api.futmail.model.NewsletterStatus;
import com.api.futmail.repository.NewsletterRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NewsletterService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");
    private static final int DEFAULT_NEWS_LIMIT = 5;
    private static final int FALLBACK_DAYS = 2;
    
    private final NewsletterRepository newsletterRepository;
    private final NewsService newsService;
    private final SubscriberService subscriberService;
    private final EmailService emailService;
    
    public NewsletterResponse createDailyNewsletter() {
        log.info("📰 Criando newsletter diária");
        
        List<NewsResponse> todaysNews = getNewsForNewsletter();
        
        Newsletter newsletter = buildDailyNewsletter(todaysNews);
        Newsletter savedNewsletter = newsletterRepository.save(newsletter);
        
        log.info("✅ Newsletter criada com ID: {}", savedNewsletter.getId());
        
        return NewsletterResponse.fromEntity(savedNewsletter);
    }
    
    public NewsletterResponse sendNewsletter(Long newsletterId) {
        Newsletter newsletter = findNewsletterById(newsletterId);
        
        validateNewsletterCanBeSent(newsletter);
        
        log.info("📤 Iniciando envio da newsletter ID: {}", newsletterId);
        
        List<SubscriberResponse> activeSubscribers = subscriberService.getActiveSubscribers();
        List<String> emails = extractEmailsFromSubscribers(activeSubscribers);
        
        newsletter.markAsStartedSending();
        newsletter.setTotalSubscribers(emails.size());
        newsletterRepository.save(newsletter);
        
        EmailSendResult sendResult = emailService.sendBulkEmails(
            emails, 
            newsletter.getSubject(), 
            newsletter.getHtmlContent()
        );
        
        newsletter.markAsSent(emails.size(), sendResult.getSent(), sendResult.getFailed());
        Newsletter updatedNewsletter = newsletterRepository.save(newsletter);
        
        log.info("✅ Newsletter enviada: {}", sendResult.getSummary());
        
        return NewsletterResponse.fromEntity(updatedNewsletter);
    }
    
    public Page<NewsletterResponse> getAllNewsletters(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return newsletterRepository.findAllOrderByCreatedAtDesc(pageable)
                .map(NewsletterResponse::fromEntity);
    }
    
    private List<NewsResponse> getNewsForNewsletter() {
        List<NewsResponse> todaysNews = newsService.getTodaysNews();
        
        if (todaysNews.isEmpty()) {
            log.info("📄 Nenhuma notícia hoje, buscando dos últimos {} dias", FALLBACK_DAYS);
            todaysNews = newsService.getRecentNews(FALLBACK_DAYS);
        }
        
        return todaysNews;
    }
    
    private Newsletter buildDailyNewsletter(List<NewsResponse> news) {
        return Newsletter.builder()
                .subject(generateSubject())
                .content(generateTextContent(news))
                .htmlContent(generateHtmlContent(news))
                .status(NewsletterStatus.DRAFT)
                .build();
    }
    
    private Newsletter findNewsletterById(Long newsletterId) {
        return newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new IllegalArgumentException("Newsletter não encontrada"));
    }
    
    private void validateNewsletterCanBeSent(Newsletter newsletter) {
        if (!newsletter.canBeSent()) {
            throw new IllegalArgumentException("Newsletter já foi enviada ou está em processo de envio");
        }
    }
    
    private List<String> extractEmailsFromSubscribers(List<SubscriberResponse> subscribers) {
        return subscribers.stream()
                .filter(SubscriberResponse::canReceiveEmails)
                .map(SubscriberResponse::getEmail)
                .toList();
    }
    
    private String generateSubject() {
        LocalDate today = LocalDate.now();
        return String.format("⚽ Futmail - %s | Sua dose diária de futebol", 
                today.format(DATE_FORMATTER));
    }
    
    private String generateTextContent(List<NewsResponse> news) {
        StringBuilder content = new StringBuilder();
        content.append("🏆 FUTMAIL - SUA NEWSLETTER DIÁRIA DE FUTEBOL\n\n");
        
        if (news.isEmpty()) {
            content.append("Nenhuma notícia disponível hoje.\n");
            return content.toString();
        }
        
        content.append("📰 PRINCIPAIS NOTÍCIAS DO DIA:\n\n");
        
        news.stream()
                .limit(DEFAULT_NEWS_LIMIT)
                .forEach(newsItem -> appendNewsToContent(content, newsItem));
        
        content.append("Acesse nosso site para mais notícias!\n");
        content.append("\nObrigado por assinar o Futmail! ⚽");
        
        return content.toString();
    }
    
    private void appendNewsToContent(StringBuilder content, NewsResponse newsItem) {
        content.append(String.format("• %s\n", newsItem.getTitle()));
        if (newsItem.getSummary() != null) {
            content.append(String.format("  %s\n", newsItem.getSummary()));
        }
        content.append("\n");
    }
    
    private String generateHtmlContent(List<NewsResponse> news) {
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader());
        
        if (news.isEmpty()) {
            html.append("<p>Nenhuma notícia disponível hoje.</p>");
        } else {
            html.append("<h2>📰 Principais notícias do dia:</h2>");
            
            news.stream()
                    .limit(DEFAULT_NEWS_LIMIT)
                    .forEach(newsItem -> appendNewsToHtml(html, newsItem));
        }
        
        html.append(getHtmlFooter());
        
        return html.toString();
    }
    
    private void appendNewsToHtml(StringBuilder html, NewsResponse newsItem) {
        html.append("<div class='news-item'>");
        html.append("<div class='news-title'>").append(newsItem.getTitle()).append("</div>");
        if (newsItem.getSummary() != null) {
            html.append("<div class='news-summary'>").append(newsItem.getSummary()).append("</div>");
        }
        html.append("</div>");
    }
    
    private String getHtmlHeader() {
        return """
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
            """;
    }
    
    private String getHtmlFooter() {
        return """
                </div>
                <div class="footer">
                    <p>Obrigado por assinar o Futmail! ⚽</p>
                    <p><a href="#">Descadastrar</a></p>
                </div>
            </body>
            </html>
            """;
    }
}
