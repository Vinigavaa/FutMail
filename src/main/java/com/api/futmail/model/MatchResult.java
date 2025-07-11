package com.api.futmail.model;

// model/MatchResult.java

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchResult {
    private String homeTeam;
    private String awayTeam;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private LocalDateTime matchDate;
    private String competition;

    public MatchResult(String homeTeam, String awayTeam, Integer homeScore,
                       Integer awayScore, String status, LocalDateTime matchDate, String competition) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
        this.matchDate = matchDate;
        this.competition = competition;
    }

    // Getters
    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public String getCompetition() {
        return competition;
    }

    // Setters
    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String toNewsTitle() {
        if ("Finalizado".equals(status) && homeScore != null && awayScore != null) {
            return String.format("⚽ %s %d x %d %s - %s",
                    homeTeam, homeScore, awayScore, awayTeam, competition);
        } else if ("Ao Vivo".equals(status) || "Em Andamento".equals(status)) {
            String scoreText = (homeScore != null && awayScore != null)
                    ? String.format(" %d x %d", homeScore, awayScore) : "";
            return String.format("🔴 AO VIVO: %s%s %s - %s",
                    homeTeam, scoreText, awayTeam, competition);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            return String.format("📅 %s x %s - %s (%s)",
                    homeTeam, awayTeam, competition, matchDate.format(formatter));
        }
    }

    public String toNewsSummary() {
        if ("Finalizado".equals(status) && homeScore != null && awayScore != null) {
            String winner = homeScore > awayScore ? homeTeam :
                    awayScore > homeScore ? awayTeam : "Empate";
            if (!"Empate".equals(winner)) {
                return String.format("%s venceu %s por %d a %d pela %s.",
                        winner, homeScore > awayScore ? awayTeam : homeTeam,
                        Math.max(homeScore, awayScore), Math.min(homeScore, awayScore), competition);
            } else {
                return String.format("%s e %s empataram em %d a %d pela %s.",
                        homeTeam, awayTeam, homeScore, awayScore, competition);
            }
        } else if ("Ao Vivo".equals(status) || "Em Andamento".equals(status)) {
            return String.format("🔴 %s e %s estão jogando AGORA pela %s.",
                    homeTeam, awayTeam, competition);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");
            return String.format("%s enfrentará %s no dia %s pela %s.",
                    homeTeam, awayTeam, matchDate.format(formatter), competition);
        }
    }
}
