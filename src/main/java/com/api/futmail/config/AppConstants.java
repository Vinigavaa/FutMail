package com.api.futmail.config;

public final class AppConstants {
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    
    // Newsletter
    public static final int MAX_NEWS_PER_NEWSLETTER = 5;
    public static final int NEWSLETTER_FALLBACK_DAYS = 2;
    
    // Data Collection
    public static final int MAX_MATCHES_PER_COMPETITION = 3;
    public static final int MAX_UPCOMING_MATCHES = 2;
    public static final int DEFAULT_RECENT_DAYS = 1;
    
    // Email
    public static final double EMAIL_SUCCESS_RATE_THRESHOLD = 0.95;
    public static final int EMAIL_SEND_DELAY_MS = 100;
    public static final int MAX_EMAIL_CONTENT_PREVIEW = 100;
    
    // Validation
    public static final int MAX_NEWS_TITLE_LENGTH = 500;
    public static final int MAX_NEWS_SUMMARY_LENGTH = 1000;
    public static final int MAX_RECENT_DAYS_FILTER = 30;
    
    // Date Formats
    public static final String DATE_FORMAT_PATTERN = "dd/MM";
    public static final String DATE_TIME_FORMAT_PATTERN = "dd/MM HH:mm";
    public static final String DATE_TIME_FULL_FORMAT_PATTERN = "dd/MM 'às' HH:mm";
    
    // API Keys (devem ser movidos para application.properties)
    public static final String FOOTBALL_API_KEY = "cc426f864ddb460db46bc5ce071e4587";
    public static final String FOOTBALL_API_BASE_URL = "https://api.football-data.org/v4";
    
    // Competition Codes
    public static final String BRASILEIRAO_CODE = "BSA";
    public static final String PREMIER_LEAGUE_CODE = "PL";
    public static final String LA_LIGA_CODE = "PD";
    public static final String CHAMPIONS_LEAGUE_CODE = "CL";
    public static final String SERIE_A_CODE = "SA";
    public static final String BUNDESLIGA_CODE = "BL1";
    
    // Email Templates
    public static final String DEFAULT_FROM_EMAIL = "newsletter@futmail.com";
    public static final String DEFAULT_FROM_NAME = "Futmail Newsletter";
    
    // System Messages
    public static final String SUCCESS_NEWS_CREATED = "✅ Notícia criada com sucesso";
    public static final String SUCCESS_NEWSLETTER_CREATED = "✅ Newsletter criada com sucesso";
    public static final String SUCCESS_NEWSLETTER_SENT = "✅ Newsletter enviada com sucesso";
    public static final String SUCCESS_SUBSCRIBER_ADDED = "✅ Assinante cadastrado com sucesso";
    
    public static final String ERROR_NEWS_DUPLICATE = "Notícia já existe";
    public static final String ERROR_NEWSLETTER_NOT_FOUND = "Newsletter não encontrada";
    public static final String ERROR_NEWSLETTER_ALREADY_SENT = "Newsletter já foi enviada";
    public static final String ERROR_SUBSCRIBER_DUPLICATE = "Email já cadastrado";
    public static final String ERROR_SUBSCRIBER_NOT_FOUND = "Email não encontrado";
    
    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
} 