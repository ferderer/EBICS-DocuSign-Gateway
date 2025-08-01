package de.ferderer.ebicsdocusign.gateway.domain.payments.model;

import java.time.LocalDateTime;

public record IncomingPayment(
    Long id,
    String transactionId,
    LocalDateTime receivedAt,
    String amount,
    String currency,
    String debtorName,
    String debtorAccount,
    String creditorName,
    String creditorAccount,
    String remittanceInfo,
    String endToEndId,
    String status,
    String bankName,
    String hostId
) {}
