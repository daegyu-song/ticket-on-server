package com.dg.ticketonserver.concert.repository;

import com.dg.ticketonserver.concert.domain.ConcertLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConcertLikeRepository extends JpaRepository<ConcertLike, Long> {

    boolean existsByConcertIdAndUserId(Long concertId, Long userId);

    @Modifying
    @Query("DELETE FROM ConcertLike cl WHERE cl.concert.id = :concertId AND cl.user.id = :userId")
    int deleteByConcertIdAndUserId(@Param("concertId") Long concertId, @Param("userId") Long userId);

    @Query("SELECT cl.concert.id FROM ConcertLike cl WHERE cl.user.id = :userId AND cl.concert.id IN :concertIds")
    List<Long> findLikedConcertIds(@Param("userId") Long userId, @Param("concertIds") List<Long> concertIds);
}
