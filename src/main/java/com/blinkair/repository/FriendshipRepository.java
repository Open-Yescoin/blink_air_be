package com.blinkair.repository;

import com.blinkair.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE f.user1Id = :userId OR f.user2Id = :userId ORDER BY f.createdAt DESC")
    List<Friendship> findByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f WHERE (f.user1Id = :u1 AND f.user2Id = :u2) OR (f.user1Id = :u2 AND f.user2Id = :u1)")
    Optional<Friendship> findByUserPair(@Param("u1") Long u1, @Param("u2") Long u2);
}
