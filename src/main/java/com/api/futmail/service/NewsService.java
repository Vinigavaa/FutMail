// service/NewsService.java
package com.api.futmail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.futmail.dto.NewsRequest;
import com.api.futmail.dto.NewsResponse;
import com.api.futmail.model.*;
import com.api.futmail.repository.NewsRepository;
import com.api.futmail.service.strategy.NewsCategorizationStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NewsService {
    
    private static final int MAX_MATCHES_PER_COMPETITION = 3;
    private static final int MAX_UPCOMING_MATCHES = 2;
    private static final int DEFAULT_RECENT_DAYS = 2;
    
    private final NewsRepository newsRepository;
    private final FootballDataService footballDataService;
    private final NewsCategorizationStrategy categorizationStrategy;
    
    public NewsResponse createNews(NewsRequest request) {
        validateNewsRequest(request);
        
        News news = buildNewsFromRequest(request);
        
        if (isDuplicateNews(news)) {
            throw new IllegalArgumentException("Notícia já existe");
        }
        
        News savedNews = newsRepository.save(news);
        log.info("✅ Notícia criada: {}", savedNews.getTitle());
        
        return NewsResponse.fromEntity(savedNews);
    }
    
    public List<NewsResponse> getTodaysNews() {
        LocalDateTime[] todayRange = getTodayRange();
        
        return newsRepository.findTodaysNews(todayRange[0], todayRange[1])
                .stream()
                .map(NewsResponse::fromEntity)
                .toList();
    }
    
    public List<NewsResponse> getNewsByCategory(NewsCategory category) {
        return newsRepository.findByCategory(category)
                .stream()
                .map(NewsResponse::fromEntity)
                .toList();
    }
    
    public Page<NewsResponse> getAllNews(int page, int size) {
        return newsRepository.findActiveNews(PageRequest.of(page, size))
                .map(NewsResponse::fromEntity);
    }
    
    public List<NewsResponse> getRecentNews(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return newsRepository.findActiveNewsAfterDate(since)
                .stream()
                .map(NewsResponse::fromEntity)
                .toList();
    }
    
    public CollectionResult collectTodaysNews() {
        log.info("🔍 Iniciando coleta automática de notícias");
        
        NewsCollectionContext context = new NewsCollectionContext();
        collectFromMultipleSources(context);
        
        CollectionResult result = CollectionResult.of(context.getCreated(), context.getDuplicates());
        log.info("🎉 Coleta finalizada: {}", result.getSummary());
        
        return result;
    }
    
    private void collectFromMultipleSources(NewsCollectionContext context) {
        collectTodaysMatches(context);
        collectRecentMatches(context);
        collectCompetitionMatches(context);
        collectUpcomingMatches(context);
        collectStandings(context);
        collectBrazileirao(context);
    }
    
    private void collectTodaysMatches(NewsCollectionContext context) {
        List<MatchResult> matches = footballDataService.getTodaysMatches();
        log.info("📥 Encontrados {} jogos hoje", matches.size());
        processMatches(matches, context);
    }
    
    private void collectRecentMatches(NewsCollectionContext context) {
        List<MatchResult> matches = footballDataService.getRecentMatches();
        log.info("📥 Encontrados {} jogos recentes", matches.size());
        processMatches(matches, context);
    }
    
    private void collectCompetitionMatches(NewsCollectionContext context) {
        collectPremierLeagueMatches(context);
        collectLaLigaMatches(context);
        collectChampionsLeagueMatches(context);
    }
    
    private void collectPremierLeagueMatches(NewsCollectionContext context) {
        List<MatchResult> matches = footballDataService.getPremierLeagueMatches();
        log.info("📥 Encontrados {} jogos da Premier League", matches.size());
        processLimitedMatches(matches, MAX_MATCHES_PER_COMPETITION, context);
    }
    
    private void collectLaLigaMatches(NewsCollectionContext context) {
        List<MatchResult> matches = footballDataService.getLaLigaMatches();
        log.info("📥 Encontrados {} jogos da La Liga", matches.size());
        processLimitedMatches(matches, MAX_MATCHES_PER_COMPETITION, context);
    }
    
    private void collectChampionsLeagueMatches(NewsCollectionContext context) {
        List<MatchResult> matches = footballDataService.getChampionsLeagueMatches();
        log.info("📥 Encontrados {} jogos da Champions League", matches.size());
        processLimitedMatches(matches, MAX_MATCHES_PER_COMPETITION, context);
    }
    
    private void collectUpcomingMatches(NewsCollectionContext context) {
        List<MatchResult> matches = footballDataService.getUpcomingMatches();
        log.info("📥 Encontrados {} próximos jogos", matches.size());
        processLimitedMatches(matches, MAX_UPCOMING_MATCHES, context);
    }
    
    private void collectStandings(NewsCollectionContext context) {
        Optional.ofNullable(footballDataService.getPremierLeagueStandings())
                .ifPresent(standings -> processStandings(standings, context));
    }
    
    private void collectBrazileirao(NewsCollectionContext context) {
        try {
            List<MatchResult> matches = footballDataService.getBrasileirao2024Matches();
            log.info("📥 Encontrados {} jogos do Brasileirão", matches.size());
            processLimitedMatches(matches, MAX_MATCHES_PER_COMPETITION, context);
        } catch (Exception e) {
            log.warn("⚠️ Brasileirão não disponível: {}", e.getMessage());
        }
    }
    
    private void processMatches(List<MatchResult> matches, NewsCollectionContext context) {
        matches.forEach(match -> processMatch(match, context));
    }
    
    private void processLimitedMatches(List<MatchResult> matches, int limit, NewsCollectionContext context) {
        matches.stream()
                .limit(limit)
                .forEach(match -> processMatch(match, context));
    }
    
    private void processMatch(MatchResult match, NewsCollectionContext context) {
        try {
            NewsRequest request = createNewsRequestFromMatch(match);
            createNews(request);
            context.incrementCreated();
            log.debug("✅ Notícia criada: {}", match.toNewsTitle());
        } catch (IllegalArgumentException e) {
            context.incrementDuplicates();
            log.debug("⚠️ Notícia duplicada: {}", match.toNewsTitle());
        } catch (Exception e) {
            log.error("❌ Erro ao processar jogo: {}", e.getMessage());
        }
    }
    
    private void processStandings(BrasileraoStandings standings, NewsCollectionContext context) {
        try {
            NewsRequest request = createNewsRequestFromStandings(standings);
            createNews(request);
            context.incrementCreated();
            log.debug("✅ Classificação criada: {}", standings.toNewsTitle());
        } catch (IllegalArgumentException e) {
            context.incrementDuplicates();
            log.debug("⚠️ Classificação duplicada: {}", standings.toNewsTitle());
        } catch (Exception e) {
            log.error("❌ Erro ao processar classificação: {}", e.getMessage());
        }
    }
    
    private NewsRequest createNewsRequestFromMatch(MatchResult match) {
        return NewsRequest.builder()
                .title(match.toNewsTitle())
                .content(match.toNewsSummary())
                .summary(match.toNewsSummary())
                .category(categorizationStrategy.categorizeMatch(match))
                .sourceName("Football-Data.org")
                .build();
    }
    
    private NewsRequest createNewsRequestFromStandings(BrasileraoStandings standings) {
        return NewsRequest.builder()
                .title(standings.toNewsTitle())
                .content(standings.toNewsSummary())
                .summary(standings.toNewsSummary())
                .category(categorizationStrategy.categorizeStandings(standings))
                .sourceName("Football-Data.org")
                .build();
    }
    
    private void validateNewsRequest(NewsRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Conteúdo é obrigatório");
        }
        if (request.getCategory() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
    }
    
    private News buildNewsFromRequest(NewsRequest request) {
        return News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .summary(request.getSummary())
                .category(request.getCategory())
                .sourceUrl(request.getSourceUrl())
                .sourceName(request.getSourceName())
                .publishedAt(LocalDateTime.now())
                .build();
    }
    
    private boolean isDuplicateNews(News news) {
        String contentHash = generateContentHash(news);
        return newsRepository.existsByContentHash(contentHash);
    }
    
    private String generateContentHash(News news) {
        return Integer.toHexString((news.getTitle() + news.getContent()).hashCode());
    }
    
    private LocalDateTime[] getTodayRange() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return new LocalDateTime[]{startOfDay, endOfDay};
    }
    
    // Classe auxiliar para contexto de coleta
    private static class NewsCollectionContext {
        private int created = 0;
        private int duplicates = 0;
        
        public void incrementCreated() { created++; }
        public void incrementDuplicates() { duplicates++; }
        public int getCreated() { return created; }
        public int getDuplicates() { return duplicates; }
    }
}