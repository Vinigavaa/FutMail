package com.api.futmail.model;

import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");
    
    private String homeTeam;
    private String awayTeam;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private LocalDateTime matchDate;
    private String competition;
    
    public String toNewsTitle() {
        if (isFinished()) {
            return createFinishedMatchTitle();
        }
        
        if (isLive()) {
            return createLiveMatchTitle();
        }
        
        return createScheduledMatchTitle();
    }
    
    public String toNewsSummary() {
        if (isFinished()) {
            return createFinishedMatchSummary();
        }
        
        if (isLive()) {
            return createLiveMatchSummary();
        }
        
        return createScheduledMatchSummary();
    }
    
    private boolean isFinished() {
        return "Finalizado".equals(status);
    }
    
    private boolean isLive() {
        return "Ao Vivo".equals(status) || "Em Andamento".equals(status);
    }
    
    private boolean hasScore() {
        return homeScore != null && awayScore != null;
    }
    
    private String createFinishedMatchTitle() {
        if (hasScore()) {
            return String.format("⚽ %s %d x %d %s - %s", 
                homeTeam, homeScore, awayScore, awayTeam, competition);
        }
        return String.format("⚽ %s x %s - %s", homeTeam, awayTeam, competition);
    }
    
    private String createLiveMatchTitle() {
        String scoreText = hasScore() ? String.format(" %d x %d", homeScore, awayScore) : "";
        return String.format("🔴 AO VIVO: %s%s %s - %s", 
            homeTeam, scoreText, awayTeam, competition);
    }
    
    private String createScheduledMatchTitle() {
        return String.format("📅 %s x %s - %s (%s)", 
            homeTeam, awayTeam, competition, matchDate.format(DATE_TIME_FORMATTER));
    }
    
    private String createFinishedMatchSummary() {
        if (!hasScore()) {
            return String.format("%s enfrentou %s pela %s.", homeTeam, awayTeam, competition);
        }
        
        String winner = determineWinner();
        if ("Empate".equals(winner)) {
            return String.format("%s e %s empataram em %d a %d pela %s.", 
                homeTeam, awayTeam, homeScore, awayScore, competition);
        }
        
        String loser = homeScore > awayScore ? awayTeam : homeTeam;
        int winnerScore = Math.max(homeScore, awayScore);
        int loserScore = Math.min(homeScore, awayScore);
        
        return String.format("%s venceu %s por %d a %d pela %s.", 
            winner, loser, winnerScore, loserScore, competition);
    }
    
    private String createLiveMatchSummary() {
        return String.format("🔴 %s e %s estão jogando AGORA pela %s.", 
            homeTeam, awayTeam, competition);
    }
    
    private String createScheduledMatchSummary() {
        return String.format("%s enfrentará %s no dia %s pela %s.", 
            homeTeam, awayTeam, matchDate.format(DATE_FORMATTER), competition);
    }
    
    private String determineWinner() {
        if (homeScore > awayScore) {
            return homeTeam;
        } else if (awayScore > homeScore) {
            return awayTeam;
        }
        return "Empate";
    }
    
    public boolean isImportantMatch() {
        return competition.toLowerCase().contains("champions") || 
               competition.toLowerCase().contains("final") ||
               competition.toLowerCase().contains("clássico");
    }
}
