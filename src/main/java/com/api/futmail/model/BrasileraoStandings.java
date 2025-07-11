package com.api.futmail.model;

// model/BrasileraoStandings.java

import java.util.List;

public class BrasileraoStandings {
    private List<TeamStanding> standings;

    public BrasileraoStandings(List<TeamStanding> standings) {
        this.standings = standings;
    }

    public List<TeamStanding> getStandings() {
        return standings;
    }

    public void setStandings(List<TeamStanding> standings) {
        this.standings = standings;
    }

    public String toNewsTitle() {
        return "📊 Classificação Atualizada do Brasileirão Série A";
    }

    public String toNewsSummary() {
        if (standings.isEmpty()) return "Classificação não disponível.";

        TeamStanding leader = standings.get(0);
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("%s lidera o Brasileirão com %d pontos. ",
                leader.getTeamName(), leader.getPoints()));

        // Top 4 (G4)
        summary.append("G4: ");
        for (int i = 0; i < Math.min(4, standings.size()); i++) {
            if (i > 0) summary.append(", ");
            summary.append(standings.get(i).getTeamName());
        }

        return summary.toString();
    }
}
