package com.dg.ticketonserver.concert.domain;

import com.dg.ticketonserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "concerts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseTimeEntity {

    @Id
    @Column(name = "concert_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 100)
    private String venue;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
