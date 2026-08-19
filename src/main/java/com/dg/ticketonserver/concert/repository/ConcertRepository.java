package com.dg.ticketonserver.concert.repository;

import com.dg.ticketonserver.concert.domain.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    Page<Concert> findByEndDateGreaterThanEqual(LocalDate date, Pageable pageable);

    @Modifying
    @Query("UPDATE Concert c SET c.likeCount = c.likeCount + 1 WHERE c.id = :concertId")
    void increaseLikeCount(@Param("concertId") Long concertId);

    @Modifying
    @Query("UPDATE Concert c SET c.likeCount = c.likeCount - 1 WHERE c.id = :concertId AND c.likeCount > 0")
    void decreaseLikeCount(@Param("concertId") Long concertId);
}
