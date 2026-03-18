package com.blinkair.repository;

import com.blinkair.entity.MatchQueue;
import com.blinkair.entity.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchQueueRepository extends JpaRepository<MatchQueue, Long> {

    Optional<MatchQueue> findByUserIdAndStatus(Long userId, MatchStatus status);

    @Query("SELECT m FROM MatchQueue m WHERE m.status = 'WAITING' ORDER BY m.createdAt ASC")
    List<MatchQueue> findAllWaiting();

    @Query("SELECT m FROM MatchQueue m WHERE m.userId = :userId AND m.status IN ('WAITING', 'MATCHED') ORDER BY m.createdAt DESC")
    Optional<MatchQueue> findActiveByUserId(@Param("userId") Long userId);
}
