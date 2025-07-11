package com.api.futmail.service.strategy;

import com.api.futmail.model.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Set;

@Component
public class NewsCategorizationStrategy {
    
    private static final Set<String> INTERNATIONAL_COMPETITIONS = Set.of(
        "champions", "europa", "premier", "liga", "bundesliga", "serie", "ligue"
    );
    
    private static final Set<String> LIVE_STATUSES = Set.of(
        "Ao Vivo", "Em Andamento"
    );
    
    private static final Set<String> FINISHED_STATUSES = Set.of(
        "Finalizado"
    );
    
    private static final Set<String> BRAZILIAN_KEYWORDS = Set.of(
        "brasileirão", "brasileiro", "série a", "copa do brasil", "libertadores"
    );
    
    public NewsCategory categorizeMatch(MatchResult match) {
        if (isLiveMatch(match) || isFinishedMatch(match)) {
            return NewsCategory.RESULTS;
        }
        
        if (isBrazilianMatch(match)) {
            return NewsCategory.BRAZILIAN_LEAGUE;
        }
        
        if (isInternationalCompetition(match)) {
            return NewsCategory.INTERNATIONAL;
        }
        
        return NewsCategory.GENERAL;
    }
    
    public NewsCategory categorizeStandings(BrasileraoStandings standings) {
        if (isBrazilianStandings(standings)) {
            return NewsCategory.BRAZILIAN_LEAGUE;
        }
        
        return NewsCategory.INTERNATIONAL;
    }
    
    public NewsCategory categorizeByKeywords(String title, String content) {
        String combinedText = (title + " " + content).toLowerCase();
        
        if (containsTransferKeywords(combinedText)) {
            return NewsCategory.TRANSFERS;
        }
        
        if (containsRumorKeywords(combinedText)) {
            return NewsCategory.RUMORS;
        }
        
        if (containsBrazilianKeywords(combinedText)) {
            return NewsCategory.BRAZILIAN_LEAGUE;
        }
        
        if (containsInternationalKeywords(combinedText)) {
            return NewsCategory.INTERNATIONAL;
        }
        
        return NewsCategory.GENERAL;
    }
    
    private boolean isLiveMatch(MatchResult match) {
        return LIVE_STATUSES.contains(match.getStatus());
    }
    
    private boolean isFinishedMatch(MatchResult match) {
        return FINISHED_STATUSES.contains(match.getStatus());
    }
    
    private boolean isBrazilianMatch(MatchResult match) {
        String competition = match.getCompetition().toLowerCase();
        return BRAZILIAN_KEYWORDS.stream()
                .anyMatch(competition::contains);
    }
    
    private boolean isInternationalCompetition(MatchResult match) {
        String competition = match.getCompetition().toLowerCase();
        return INTERNATIONAL_COMPETITIONS.stream()
                .anyMatch(competition::contains);
    }
    
    private boolean isBrazilianStandings(BrasileraoStandings standings) {
        String title = standings.toNewsTitle().toLowerCase();
        return BRAZILIAN_KEYWORDS.stream()
                .anyMatch(title::contains);
    }
    
    private boolean containsTransferKeywords(String text) {
        return text.contains("transferência") || 
               text.contains("contratação") || 
               text.contains("venda") ||
               text.contains("empréstimo") ||
               text.contains("renovação");
    }
    
    private boolean containsRumorKeywords(String text) {
        return text.contains("rumor") || 
               text.contains("especulação") || 
               text.contains("pode") ||
               text.contains("talvez") ||
               text.contains("interesse");
    }
    
    private boolean containsBrazilianKeywords(String text) {
        return BRAZILIAN_KEYWORDS.stream()
                .anyMatch(text::contains);
    }
    
    private boolean containsInternationalKeywords(String text) {
        return INTERNATIONAL_COMPETITIONS.stream()
                .anyMatch(text::contains);
    }
} 