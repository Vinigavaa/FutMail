package com.api.futmail.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.futmail.model.Newsletter;
import com.api.futmail.model.NewsletterStatus;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    
    @Query("SELECT n FROM Newsletter n ORDER BY n.createdAt DESC")
    Page<Newsletter> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT n FROM Newsletter n WHERE n.status = :status ORDER BY n.createdAt DESC")
    List<Newsletter> findByStatus(@Param("status") NewsletterStatus status);
    
    @Query("SELECT n FROM Newsletter n WHERE DATE(n.sentAt) = CURRENT_DATE")
    List<Newsletter> findTodaysSentNewsletters();
}
