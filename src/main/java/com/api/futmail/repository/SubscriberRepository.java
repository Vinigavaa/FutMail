package com.api.futmail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.futmail.model.Subscriber;
import com.api.futmail.model.SubscriptionStatus;

// repository/SubscriberRepository.java
@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    
    Optional<Subscriber> findByEmail(String email);
    
    @Query("SELECT s FROM Subscriber s WHERE s.status = :status")
    List<Subscriber> findByStatus(@Param("status") SubscriptionStatus status);
    
    boolean existsByEmail(String email);
}
