package de.ferderer.ebicsdocusign.gateway.domain.payments.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Payment")
@Table(name = "payments")
@Getter
@Setter
public class PaymentEntity {
    public enum PaymentStatus {
        RECEIVED, PROCESSED, CONTRACT_PENDING, CONTRACT_SENT, CONTRACT_SIGNED, ERROR
    }

    @Id
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String transactionId;
    
    @Column(nullable = false)
    private LocalDateTime receivedAt;
    
    private LocalDate valueDate;
    private LocalDate bookingDate;
    
    @Column(nullable = false)
    private String amount;
    
    @Column(nullable = false)
    private String currency;
    
    @Column(nullable = false)
    private String debtorName;
    
    private String debtorAccount;
    private String creditorName;
    private String creditorAccount;
    private String remittanceInfo;
    private String endToEndId;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.RECEIVED;
    
    @Column(nullable = false)
    private Long connectionId;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PaymentEntity other && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
