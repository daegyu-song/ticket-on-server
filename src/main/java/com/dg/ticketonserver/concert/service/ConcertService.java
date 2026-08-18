package com.dg.ticketonserver.concert.service;

import com.dg.ticketonserver.concert.dto.ConcertResponse;
import com.dg.ticketonserver.concert.dto.ConcertSortType;
import com.dg.ticketonserver.concert.repository.ConcertRepository;
import com.dg.ticketonserver.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;

    public PageResponse<ConcertResponse> getConcerts(Pageable pageable, ConcertSortType concertSortType) {
        Pageable request = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), concertSortType.getSort());

        return PageResponse.from(
                concertRepository.findByEndDateGreaterThanEqual(LocalDate.now(), request)
                .map(ConcertResponse::from)
        );
    }
}
