package com.api.futmail.model;

public class TeamStanding {
    private int position;
    private String teamName;
    private int points;
    private int playedGames;
    private int won;
    private int draw;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;

    public TeamStanding(int position, String teamName, int points, int playedGames,
                        int won, int draw, int lost, int goalsFor, int goalsAgainst, int goalDifference) {
        this.position = position;
        this.teamName = teamName;
        this.points = points;
        this.playedGames = playedGames;
        this.won = won;
        this.draw = draw;
        this.lost = lost;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalDifference = goalDifference;
    }

    // Getters
    public int getPosition() {
        return position;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getPoints() {
        return points;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public int getWon() {
        return won;
    }

    public int getDraw() {
        return draw;
    }

    public int getLost() {
        return lost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    // Setters
    public void setPosition(int position) {
        this.position = position;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setPlayedGames(int playedGames) {
        this.playedGames = playedGames;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }
}
