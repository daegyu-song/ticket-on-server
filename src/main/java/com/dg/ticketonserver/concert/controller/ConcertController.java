package com.dg.ticketonserver.concert.controller;

import com.dg.ticketonserver.concert.dto.ConcertResponse;
import com.dg.ticketonserver.concert.dto.ConcertSortType;
import com.dg.ticketonserver.concert.service.ConcertService;
import com.dg.ticketonserver.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping
    public ResponseEntity<PageResponse<ConcertResponse>> getConcerts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(defaultValue = "LATEST") ConcertSortType concertSortType
    ) {
        return ResponseEntity.ok(concertService.getConcerts(pageable, concertSortType));
    }
}
