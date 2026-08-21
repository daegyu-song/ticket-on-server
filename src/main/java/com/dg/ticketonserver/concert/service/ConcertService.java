package com.dg.ticketonserver.concert.service;

import com.dg.ticketonserver.concert.domain.Concert;
import com.dg.ticketonserver.concert.dto.ConcertResponse;
import com.dg.ticketonserver.concert.dto.ConcertSortType;
import com.dg.ticketonserver.concert.repository.ConcertLikeRepository;
import com.dg.ticketonserver.concert.repository.ConcertRepository;
import com.dg.ticketonserver.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertLikeRepository concertLikeRepository;

    public PageResponse<ConcertResponse> getConcerts(Pageable pageable, ConcertSortType concertSortType, Long userId) {
        Pageable request = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), concertSortType.getSort());

        Page<Concert> concerts = concertRepository.findByEndDateGreaterThanEqual(LocalDate.now(), request);
        List<Long> concertIds = concerts.getContent().stream()
                .map(Concert::getId)
                .toList();
        Set<Long> likedConcertIds = (userId == null || concertIds.isEmpty())
                ? Set.of()
                : Set.copyOf(concertLikeRepository.findLikedConcertIds(userId, concertIds));

        return PageResponse.from(
                concerts.map(concert -> ConcertResponse.of(concert, likedConcertIds.contains(concert.getId())))
        );
    }
}
