package com.dg.ticketonserver.concert.repository;

import com.dg.ticketonserver.concert.domain.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    Page<Concert> findByEndDateGreaterThanEqual(LocalDate date, Pageable pageable);
}
