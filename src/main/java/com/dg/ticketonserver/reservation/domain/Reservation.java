package com.dg.ticketonserver.reservation.domain;

import com.dg.ticketonserver.global.domain.BaseTimeEntity;
import com.dg.ticketonserver.schedule.domain.Schedule;
import com.dg.ticketonserver.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @Column(name = "reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(nullable = false)
    private Integer totalAmount;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
