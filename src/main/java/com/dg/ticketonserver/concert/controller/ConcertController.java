package com.dg.ticketonserver.concert.controller;

import com.dg.ticketonserver.concert.dto.ConcertResponse;
import com.dg.ticketonserver.concert.dto.ConcertSortType;
import com.dg.ticketonserver.concert.service.ConcertLikeService;
import com.dg.ticketonserver.concert.service.ConcertService;
import com.dg.ticketonserver.global.dto.PageResponse;
import com.dg.ticketonserver.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertService concertService;
    private final ConcertLikeService concertLikeService;

    @GetMapping
    public ResponseEntity<PageResponse<ConcertResponse>> getConcerts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(defaultValue = "LATEST") ConcertSortType concertSortType
    ) {
        return ResponseEntity.ok(concertService.getConcerts(pageable, concertSortType));
    }

    @PostMapping("/{concertId}/like")
    public ResponseEntity<Void> likeConcert(
            @PathVariable Long concertId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        concertLikeService.likeConcert(concertId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{concertId}/like")
    public ResponseEntity<Void> unlikeConcert(
            @PathVariable Long concertId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        concertLikeService.unlikeConcert(concertId, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
