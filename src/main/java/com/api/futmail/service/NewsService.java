// service/NewsService.java
package com.api.futmail.service;

// Imports Java Standard
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Imports Spring
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Imports Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Imports do seu projeto
import com.api.futmail.dto.NewsRequest;
import com.api.futmail.dto.NewsResponse;
import com.api.futmail.model.News;
import com.api.futmail.model.NewsCategory;
import com.api.futmail.model.MatchResult;
import com.api.futmail.model.BrasileraoStandings;
import com.api.futmail.repository.NewsRepository;

@Service
@Transactional
public class NewsService {

    private final Logger logger = LoggerFactory.getLogger(NewsService.class);
    private final NewsRepository newsRepository;
    private final FootballDataService footballDataService;

    public NewsService(NewsRepository newsRepository,
                       FootballDataService footballDataService) {
        this.newsRepository = newsRepository;
        this.footballDataService = footballDataService;
    }

    public NewsResponse createNews(NewsRequest request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setSummary(request.getSummary());
        news.setCategory(request.getCategory());
        news.setSourceUrl(request.getSourceUrl());
        news.setSourceName(request.getSourceName());
        news.setPublishedAt(LocalDateTime.now());

        // Verificar duplicata
        String contentHash = Integer.toHexString(
                (request.getTitle() + request.getContent()).hashCode()
        );

        if (newsRepository.existsByContentHash(contentHash)) {
            throw new IllegalArgumentException("Notícia já existe");
        }

        news.setContentHash(contentHash);
        News saved = newsRepository.save(news);

        return NewsResponse.fromEntity(saved);
    }

    public List<NewsResponse> getTodaysNews() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return newsRepository.findTodaysNews(startOfDay, endOfDay)
                .stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<NewsResponse> getNewsByCategory(NewsCategory category) {
        return newsRepository.findByCategory(category)
                .stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<NewsResponse> getAllNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.findActiveNews(pageable)
                .map(NewsResponse::fromEntity);
    }

    public List<NewsResponse> getRecentNews(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return newsRepository.findActiveNewsAfterDate(since)
                .stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CollectionResult collectTodaysNews() {
        logger.info("🔍 Iniciando coleta automática de notícias REAIS");

        int created = 0;
        int duplicates = 0;

        try {
            // 1. Jogos de hoje
            List<MatchResult> todaysMatches = footballDataService.getTodaysMatches();
            logger.info("📥 Encontrados {} jogos hoje", todaysMatches.size());

            for (MatchResult match : todaysMatches) {
                if (createNewsFromMatch(match)) {
                    created++;
                } else {
                    duplicates++;
                }
            }

            // 2. Resultados recentes (ontem e hoje)
            List<MatchResult> recentMatches = footballDataService.getRecentMatches();
            logger.info("📥 Encontrados {} jogos recentes", recentMatches.size());

            for (MatchResult match : recentMatches) {
                if (createNewsFromMatch(match)) {
                    created++;
                } else {
                    duplicates++;
                }
            }

            // 3. Premier League (sempre disponível)
            List<MatchResult> premierLeagueMatches = footballDataService.getPremierLeagueMatches();
            logger.info("📥 Encontrados {} jogos da Premier League", premierLeagueMatches.size());

            for (MatchResult match : premierLeagueMatches.stream().limit(3).collect(Collectors.toList())) {
                if (createNewsFromMatch(match)) {
                    created++;
                } else {
                    duplicates++;
                }
            }

            // 4. La Liga
            List<MatchResult> laLigaMatches = footballDataService.getLaLigaMatches();
            logger.info("📥 Encontrados {} jogos da La Liga", laLigaMatches.size());

            for (MatchResult match : laLigaMatches.stream().limit(2).collect(Collectors.toList())) {
                if (createNewsFromMatch(match)) {
                    created++;
                } else {
                    duplicates++;
                }
            }

            // 5. Champions League
            List<MatchResult> championsMatches = footballDataService.getChampionsLeagueMatches();
            logger.info("📥 Encontrados {} jogos da Champions League", championsMatches.size());

            for (MatchResult match : championsMatches.stream().limit(2).collect(Collectors.toList())) {
                if (createNewsFromMatch(match)) {
                    created++;
                } else {
                    duplicates++;
                }
            }

            // 6. Próximos jogos importantes
            List<MatchResult> upcomingMatches = footballDataService.getUpcomingMatches();
            logger.info("📥 Encontrados {} próximos jogos", upcomingMatches.size());

            for (MatchResult match : upcomingMatches.stream().limit(2).collect(Collectors.toList())) {
                if (createNewsFromMatch(match)) {
                    created++;
                } else {
                    duplicates++;
                }
            }

            // 7. Classificação da Premier League
            BrasileraoStandings standings = footballDataService.getPremierLeagueStandings();
            if (standings != null && createNewsFromStandings(standings)) {
                created++;
            }

            // 8. Tentar Brasileirão (pode não funcionar no plano gratuito)
            try {
                List<MatchResult> brasileiraoMatches = footballDataService.getBrasileirao2024Matches();
                logger.info("📥 Encontrados {} jogos do Brasileirão", brasileiraoMatches.size());

                for (MatchResult match : brasileiraoMatches.stream().limit(2).collect(Collectors.toList())) {
                    if (createNewsFromMatch(match)) {
                        created++;
                    } else {
                        duplicates++;
                    }
                }
            } catch (Exception e) {
                logger.warn("⚠️ Brasileirão não disponível no plano gratuito: {}", e.getMessage());
            }

        } catch (Exception e) {
            logger.error("❌ Erro durante coleta: {}", e.getMessage());
        }

        logger.info("🎉 Coleta finalizada: {} criadas, {} duplicatas", created, duplicates);
        return new CollectionResult(created, duplicates);
    }

    private boolean createNewsFromMatch(MatchResult match) {
        try {
            NewsRequest request = new NewsRequest();
            request.setTitle(match.toNewsTitle());
            request.setContent(match.toNewsSummary());
            request.setSummary(match.toNewsSummary());

            // Categorizar por status e competição
            if ("Finalizado".equals(match.getStatus())) {
                request.setCategory(NewsCategory.RESULTS);
            } else if ("Agendado".equals(match.getStatus())) {
                request.setCategory(NewsCategory.GENERAL);
            } else if ("Ao Vivo".equals(match.getStatus()) || "Em Andamento".equals(match.getStatus())) {
                request.setCategory(NewsCategory.RESULTS);
            } else {
                request.setCategory(NewsCategory.GENERAL);
            }

            // Verificar se é competição internacional
            String competition = match.getCompetition().toLowerCase();
            if (competition.contains("champions") || competition.contains("europa") ||
                    competition.contains("premier") || competition.contains("liga") ||
                    competition.contains("bundesliga") || competition.contains("serie")) {
                request.setCategory(NewsCategory.INTERNATIONAL);
            }

            request.setSourceName("Football-Data.org");

            createNews(request);
            logger.info("✅ Notícia criada: {}", match.toNewsTitle());
            return true;

        } catch (IllegalArgumentException e) {
            logger.debug("⚠️ Notícia duplicada: {}", match.toNewsTitle());
            return false;
        } catch (Exception e) {
            logger.error("❌ Erro ao criar notícia do jogo: {}", e.getMessage());
            return false;
        }
    }

    private boolean createNewsFromStandings(BrasileraoStandings standings) {
        try {
            NewsRequest request = new NewsRequest();
            request.setTitle(standings.toNewsTitle());
            request.setContent(standings.toNewsSummary());
            request.setSummary(standings.toNewsSummary());

            // Verificar se é Brasileirão ou internacional
            if (standings.toNewsTitle().toLowerCase().contains("brasileirão")) {
                request.setCategory(NewsCategory.BRAZILIAN_LEAGUE);
            } else {
                request.setCategory(NewsCategory.INTERNATIONAL);
            }

            request.setSourceName("Football-Data.org");

            createNews(request);
            logger.info("✅ Classificação criada: {}", standings.toNewsTitle());
            return true;

        } catch (IllegalArgumentException e) {
            logger.debug("⚠️ Classificação duplicada: {}", standings.toNewsTitle());
            return false;
        } catch (Exception e) {
            logger.error("❌ Erro ao criar notícia da classificação: {}", e.getMessage());
            return false;
        }
    }

    // Método adicional para estatísticas
    public NewsStats getNewsStats() {
        try {
            long totalNews = newsRepository.count();
            long todayNews = getTodaysNews().size();

            // Contar por categoria
            long results = newsRepository.findByCategory(NewsCategory.RESULTS).size();
            long transfers = newsRepository.findByCategory(NewsCategory.TRANSFERS).size();
            long international = newsRepository.findByCategory(NewsCategory.INTERNATIONAL).size();
            long brazilian = newsRepository.findByCategory(NewsCategory.BRAZILIAN_LEAGUE).size();
            long general = newsRepository.findByCategory(NewsCategory.GENERAL).size();
            long rumors = newsRepository.findByCategory(NewsCategory.RUMORS).size();

            return new NewsStats(totalNews, todayNews, results, transfers,
                    international, brazilian, general, rumors);
        } catch (Exception e) {
            logger.error("Erro ao buscar estatísticas: {}", e.getMessage());
            return new NewsStats(0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    // Método para limpeza de notícias antigas
    public int cleanOldNews(int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            List<News> oldNews = newsRepository.findActiveNewsAfterDate(cutoffDate);

            int deletedCount = 0;
            for (News news : oldNews) {
                news.setActive(false);
                newsRepository.save(news);
                deletedCount++;
            }

            logger.info("🧹 Limpeza concluída: {} notícias antigas desativadas", deletedCount);
            return deletedCount;

        } catch (Exception e) {
            logger.error("Erro na limpeza de notícias antigas: {}", e.getMessage());
            return 0;
        }
    }

    // Classes internas para resultado e estatísticas
    public static class CollectionResult {
        private final int created;
        private final int duplicates;

        public CollectionResult(int created, int duplicates) {
            this.created = created;
            this.duplicates = duplicates;
        }

        public int getCreated() { return created; }
        public int getDuplicates() { return duplicates; }
        public int getTotal() { return created + duplicates; }
    }

    public static class NewsStats {
        private final long totalNews;
        private final long todayNews;
        private final long results;
        private final long transfers;
        private final long international;
        private final long brazilian;
        private final long general;
        private final long rumors;

        public NewsStats(long totalNews, long todayNews, long results, long transfers,
                         long international, long brazilian, long general, long rumors) {
            this.totalNews = totalNews;
            this.todayNews = todayNews;
            this.results = results;
            this.transfers = transfers;
            this.international = international;
            this.brazilian = brazilian;
            this.general = general;
            this.rumors = rumors;
        }

        // Getters
        public long getTotalNews() { return totalNews; }
        public long getTodayNews() { return todayNews; }
        public long getResults() { return results; }
        public long getTransfers() { return transfers; }
        public long getInternational() { return international; }
        public long getBrazilian() { return brazilian; }
        public long getGeneral() { return general; }
        public long getRumors() { return rumors; }
    }
}