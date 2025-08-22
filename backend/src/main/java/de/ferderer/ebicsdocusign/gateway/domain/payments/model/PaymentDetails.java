package de.ferderer.ebicsdocusign.gateway.domain.payments.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentDetails(
    Long id,
    String transactionId,
    LocalDateTime receivedAt,
    LocalDate valueDate,
    LocalDate bookingDate,
    String amount,
    String currency,
    String debtorName,
    String debtorAccount,
    String creditorName,
    String creditorAccount,
    String remittanceInfo,
    String endToEndId,
    String status,
    Long connectionId,
    String bankName,
    String hostId,
    LocalDateTime createdAt
) {}
