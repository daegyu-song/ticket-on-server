package com.dg.ticketonserver.payment.domain;

public enum PaymentStatus {
    IN_PROGRESS,
    SUCCEEDED,
    FAILED,
    CANCELED,
    PENDING_RECONCILE
}
