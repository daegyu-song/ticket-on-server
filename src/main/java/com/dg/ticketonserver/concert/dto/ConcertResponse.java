package com.dg.ticketonserver.concert.dto;

import com.dg.ticketonserver.concert.domain.Concert;

import java.time.LocalDate;

public record ConcertResponse(
        Long id,
        String title,
        String venue,
        LocalDate startDate,
        LocalDate endDate
) {
    public static ConcertResponse from(Concert concert) {
        return new ConcertResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getVenue(),
                concert.getStartDate(),
                concert.getEndDate()
        );
    }
}
