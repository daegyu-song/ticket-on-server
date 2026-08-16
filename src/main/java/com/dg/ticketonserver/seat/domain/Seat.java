package com.dg.ticketonserver.seat.domain;

import com.dg.ticketonserver.schedule.domain.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seats_schedule_seat",
                columnNames = {"schedule_id", "seat_number"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @Column(name = "seat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false, length = 10)
    private String seatNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatGrade grade;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE;

    private Long heldBy;

    private LocalDateTime heldUntil;

    private Long reservationId;

    @Column(nullable = false)
    private Long version = 0L;
}
