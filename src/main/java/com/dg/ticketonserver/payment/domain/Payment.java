package com.dg.ticketonserver.payment.domain;

import com.dg.ticketonserver.global.domain.BaseTimeEntity;
import com.dg.ticketonserver.reservation.domain.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payments_idempotency_key",
                        columnNames = "idempotency_key"
                ),
                @UniqueConstraint(
                        name = "uk_payments_pg_transaction_id",
                        columnNames = "pg_transaction_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, length = 64)
    private String idempotencyKey;

    @Column(nullable = false, length = 64)
    private String requestHash;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.IN_PROGRESS;

    @Column(length = 100)
    private String pgTransactionId;

    @Column(columnDefinition = "JSON")
    private String responseSnapshot;

    private String failureReason;

    private LocalDateTime approvedAt;

    @Column(nullable = false)
    private Integer retryCount = 0;
}
