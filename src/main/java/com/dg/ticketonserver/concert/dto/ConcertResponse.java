package com.dg.ticketonserver.concert.dto;

import com.dg.ticketonserver.concert.domain.Concert;

import java.time.LocalDate;

public record ConcertResponse(
        Long id,
        String title,
        String venue,
        LocalDate startDate,
        LocalDate endDate,
        int likeCount,
        boolean liked
) {
    public static ConcertResponse of(Concert concert, boolean liked) {
        return new ConcertResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getVenue(),
                concert.getStartDate(),
                concert.getEndDate(),
                concert.getLikeCount(),
                liked
        );
    }
}
