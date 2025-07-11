// service/FootballDataService.java
package com.api.futmail.service;

// Imports Spring
import org.springframework.stereotype.Service;

// Imports Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Imports OkHttp
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Imports Jackson
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// Imports Java Standard
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

// Imports das classes do projeto
import com.api.futmail.model.MatchResult;
import com.api.futmail.model.BrasileraoStandings;
import com.api.futmail.model.TeamStanding;

@Service
public class FootballDataService {

    private final Logger logger = LoggerFactory.getLogger(FootballDataService.class);
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String API_KEY = "cc426f864ddb460db46bc5ce071e4587";
    private static final String BASE_URL = "https://api.football-data.org/v4";

    // Códigos das principais competições (CORRIGIDOS conforme documentação)
    private static final String BRASILEIRAO_CODE = "BSA";      // Brasileiro Série A
    private static final String PREMIER_LEAGUE_CODE = "PL";    // Premier League
    private static final String LA_LIGA_CODE = "PD";           // La Liga (Primera División)
    private static final String CHAMPIONS_LEAGUE_CODE = "CL";  // Champions League
    private static final String SERIE_A_CODE = "SA";           // Serie A Italiana
    private static final String BUNDESLIGA_CODE = "BL1";       // Bundesliga

    public FootballDataService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<MatchResult> getTodaysMatches() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String url = BASE_URL + "/matches?dateFrom=" + today + "&dateTo=" + today;

            logger.info("🔍 Buscando jogos de hoje: {}", today);

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                return parseMatchesResponse(jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar jogos de hoje: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<MatchResult> getRecentMatches() {
        try {
            String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String url = BASE_URL + "/matches?dateFrom=" + yesterday + "&dateTo=" + today + "&status=FINISHED";

            logger.info("📊 Buscando resultados recentes");

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                List<MatchResult> matches = parseMatchesResponse(jsonResponse);
                return matches.stream()
                        .sorted((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()))
                        .limit(8)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar jogos recentes: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<MatchResult> getPremierLeagueMatches() {
        try {
            String url = BASE_URL + "/competitions/" + PREMIER_LEAGUE_CODE + "/matches?status=FINISHED";

            logger.info("🏴󠁧󠁢󠁥󠁮󠁧󠁿 Buscando resultados da Premier League");

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                List<MatchResult> matches = parseMatchesResponse(jsonResponse);
                return matches.stream()
                        .sorted((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()))
                        .limit(5)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar jogos da Premier League: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<MatchResult> getLaLigaMatches() {
        try {
            String url = BASE_URL + "/competitions/" + LA_LIGA_CODE + "/matches?status=FINISHED";

            logger.info("🇪🇸 Buscando resultados da La Liga");

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                List<MatchResult> matches = parseMatchesResponse(jsonResponse);
                return matches.stream()
                        .sorted((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()))
                        .limit(5)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar jogos da La Liga: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<MatchResult> getChampionsLeagueMatches() {
        try {
            String url = BASE_URL + "/competitions/" + CHAMPIONS_LEAGUE_CODE + "/matches?status=FINISHED";

            logger.info("🏆 Buscando resultados da Champions League");

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                List<MatchResult> matches = parseMatchesResponse(jsonResponse);
                return matches.stream()
                        .sorted((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()))
                        .limit(3)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar jogos da Champions League: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<MatchResult> getUpcomingMatches() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String nextWeek = LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String url = BASE_URL + "/matches?dateFrom=" + today + "&dateTo=" + nextWeek + "&status=SCHEDULED";

            logger.info("📅 Buscando próximos jogos");

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                List<MatchResult> matches = parseMatchesResponse(jsonResponse);
                return matches.stream().limit(10).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar próximos jogos: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public BrasileraoStandings getPremierLeagueStandings() {
        try {
            String url = BASE_URL + "/competitions/" + PREMIER_LEAGUE_CODE + "/standings";

            logger.info("📊 Buscando classificação da Premier League");

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                BrasileraoStandings standings = parseStandingsResponse(jsonResponse);
                if (standings != null) {
                    // Renomear para contexto mais genérico
                    return new BrasileraoStandings(standings.getStandings()) {
                        @Override
                        public String toNewsTitle() {
                            return "📊 Classificação Atualizada da Premier League";
                        }

                        @Override
                        public String toNewsSummary() {
                            if (getStandings().isEmpty()) return "Classificação não disponível.";

                            TeamStanding leader = getStandings().get(0);
                            StringBuilder summary = new StringBuilder();
                            summary.append(String.format("%s lidera a Premier League com %d pontos. ",
                                    leader.getTeamName(), leader.getPoints()));

                            summary.append("Top 4: ");
                            for (int i = 0; i < Math.min(4, getStandings().size()); i++) {
                                if (i > 0) summary.append(", ");
                                summary.append(getStandings().get(i).getTeamName());
                            }

                            return summary.toString();
                        }
                    };
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar classificação da Premier League: {}", e.getMessage());
        }
        return null;
    }

    // Método para tentar buscar o Brasileirão (pode não funcionar no plano gratuito)
    public List<MatchResult> getBrasileirao2024Matches() {
        try {
            String url = BASE_URL + "/competitions/" + BRASILEIRAO_CODE + "/matches?season=2024&status=FINISHED";

            logger.info("🇧🇷 Tentando buscar resultados do Brasileirão 2024");

            String jsonResponse = makeApiCall(url);
            if (jsonResponse != null) {
                List<MatchResult> matches = parseMatchesResponse(jsonResponse);
                if (matches.isEmpty()) {
                    logger.warn("⚠️ Brasileirão pode não estar disponível no plano gratuito");
                }
                return matches.stream()
                        .sorted((m1, m2) -> m2.getMatchDate().compareTo(m1.getMatchDate()))
                        .limit(5)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.warn("⚠️ Brasileirão não disponível: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    private String makeApiCall(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-Auth-Token", API_KEY)
                    .addHeader("Accept", "application/json")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    logger.debug("✅ API call successful: {}", url);
                    return jsonData;
                } else {
                    logger.warn("❌ API call failed: {} - Status: {}", url, response.code());
                    if (response.body() != null) {
                        String errorBody = response.body().string();
                        logger.warn("Response: {}", errorBody);
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("❌ Erro na chamada da API: {} - {}", url, e.getMessage());
            return null;
        }
    }

    private List<MatchResult> parseMatchesResponse(String jsonData) {
        List<MatchResult> matches = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(jsonData);
            JsonNode matchesNode = root.get("matches");

            if (matchesNode != null && matchesNode.isArray()) {
                for (JsonNode matchNode : matchesNode) {
                    MatchResult match = parseMatch(matchNode);
                    if (match != null) {
                        matches.add(match);
                    }
                }
            }

            logger.info("📥 Parsed {} matches from API", matches.size());

        } catch (Exception e) {
            logger.error("Erro ao fazer parse dos jogos: {}", e.getMessage());
        }

        return matches;
    }

    private MatchResult parseMatch(JsonNode matchNode) {
        try {
            // Times
            JsonNode homeTeamNode = matchNode.get("homeTeam");
            JsonNode awayTeamNode = matchNode.get("awayTeam");
            String homeTeam = homeTeamNode.get("name").asText();
            String awayTeam = awayTeamNode.get("name").asText();

            // Placar
            JsonNode scoreNode = matchNode.get("score");
            Integer homeScore = null;
            Integer awayScore = null;

            if (scoreNode != null && !scoreNode.get("fullTime").isNull()) {
                JsonNode fullTimeNode = scoreNode.get("fullTime");
                if (!fullTimeNode.get("home").isNull()) {
                    homeScore = fullTimeNode.get("home").asInt();
                }
                if (!fullTimeNode.get("away").isNull()) {
                    awayScore = fullTimeNode.get("away").asInt();
                }
            }

            // Status e data
            String status = matchNode.get("status").asText();
            String utcDateStr = matchNode.get("utcDate").asText();
            LocalDateTime matchDate = LocalDateTime.parse(utcDateStr.substring(0, 19));

            // Competição
            JsonNode competitionNode = matchNode.get("competition");
            String competition = competitionNode.get("name").asText();

            // Traduzir status
            String statusPt = translateStatus(status);

            return new MatchResult(homeTeam, awayTeam, homeScore, awayScore,
                    statusPt, matchDate, competition);

        } catch (Exception e) {
            logger.error("Erro ao fazer parse de um jogo: {}", e.getMessage());
            return null;
        }
    }

    private BrasileraoStandings parseStandingsResponse(String jsonData) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            JsonNode standingsArray = root.get("standings");

            if (standingsArray != null && standingsArray.isArray() && standingsArray.size() > 0) {
                JsonNode tableNode = standingsArray.get(0).get("table");
                List<TeamStanding> standings = new ArrayList<>();

                for (JsonNode teamNode : tableNode) {
                    TeamStanding standing = parseTeamStanding(teamNode);
                    if (standing != null) {
                        standings.add(standing);
                    }
                }

                return new BrasileraoStandings(standings);
            }
        } catch (Exception e) {
            logger.error("Erro ao fazer parse da classificação: {}", e.getMessage());
        }
        return null;
    }

    private TeamStanding parseTeamStanding(JsonNode teamNode) {
        try {
            int position = teamNode.get("position").asInt();
            String teamName = teamNode.get("team").get("name").asText();
            int points = teamNode.get("points").asInt();
            int playedGames = teamNode.get("playedGames").asInt();
            int won = teamNode.get("won").asInt();
            int draw = teamNode.get("draw").asInt();
            int lost = teamNode.get("lost").asInt();
            int goalsFor = teamNode.get("goalsFor").asInt();
            int goalsAgainst = teamNode.get("goalsAgainst").asInt();
            int goalDifference = teamNode.get("goalDifference").asInt();

            return new TeamStanding(position, teamName, points, playedGames,
                    won, draw, lost, goalsFor, goalsAgainst, goalDifference);
        } catch (Exception e) {
            logger.error("Erro ao fazer parse de posição na tabela: {}", e.getMessage());
            return null;
        }
    }

    private String translateStatus(String status) {
        switch (status) {
            case "FINISHED": return "Finalizado";
            case "LIVE": return "Ao Vivo";
            case "IN_PLAY": return "Em Andamento";
            case "PAUSED": return "Pausado";
            case "SCHEDULED": return "Agendado";
            case "POSTPONED": return "Adiado";
            case "CANCELLED": return "Cancelado";
            case "SUSPENDED": return "Suspenso";
            default: return status;
        }
    }
}