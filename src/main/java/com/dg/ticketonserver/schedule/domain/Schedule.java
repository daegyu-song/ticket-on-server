package com.dg.ticketonserver.schedule.domain;

import com.dg.ticketonserver.concert.domain.Concert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "schedules",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_schedules_concert_round",
                columnNames = {"concert_id", "round_number"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(nullable = false)
    private Integer roundNumber;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime openAt;
}
