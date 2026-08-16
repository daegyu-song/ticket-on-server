package com.dg.ticketonserver.reservation.domain;

import com.dg.ticketonserver.seat.domain.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "reservation_seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reservation_seats_reservation_seat",
                columnNames = {"reservation_id", "seat_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {

    @Id
    @Column(name = "reservation_seat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private Integer price;
}
