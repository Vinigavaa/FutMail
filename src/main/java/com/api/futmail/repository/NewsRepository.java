package com.api.futmail.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.futmail.model.News;
import com.api.futmail.model.NewsCategory;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    @Query("SELECT n FROM News n WHERE n.active = true AND n.publishedAt >= :date ORDER BY n.publishedAt DESC")
    List<News> findActiveNewsAfterDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT n FROM News n WHERE n.active = true AND n.category = :category ORDER BY n.publishedAt DESC")
    List<News> findByCategory(@Param("category") NewsCategory category);
    
    boolean existsByContentHash(String contentHash);
    
    @Query("SELECT n FROM News n WHERE n.active = true ORDER BY n.publishedAt DESC")
    Page<News> findActiveNews(org.springframework.data.domain.Pageable pageable);
    
    // CORREÇÃO: Mudança na query para funcionar com H2 e PostgreSQL
    @Query("SELECT n FROM News n WHERE n.active = true AND n.publishedAt >= :startOfDay AND n.publishedAt <= :endOfDay ORDER BY n.publishedAt DESC")
    List<News> findTodaysNews(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
