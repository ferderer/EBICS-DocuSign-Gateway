package de.ferderer.ebicsdocusign.gateway.domain.payments.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<PaymentEntity, Long>, PagingAndSortingRepository<PaymentEntity, Long> {
    
    @Query("SELECT new de.ferderer.ebicsdocusign.gateway.domain.payments.model.IncomingPayment(" +
           "p.id, p.transactionId, p.receivedAt, p.amount, p.currency, p.debtorName, p.debtorAccount, " +
           "p.creditorName, p.creditorAccount, p.remittanceInfo, p.endToEndId, CAST(p.status AS string), " +
           "e.bankName, e.hostId) " +
           "FROM Payment p JOIN EbicsConnection e ON p.connectionId = e.id " +
           "ORDER BY p.receivedAt DESC LIMIT :limit")
    List<IncomingPayment> findRecentIncomingPayments(@Param("limit") int limit);

    @Query("SELECT new de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentDetails(" +
           "p.id, p.transactionId, p.receivedAt, p.valueDate, p.bookingDate, p.amount, p.currency, " +
           "p.debtorName, p.debtorAccount, p.creditorName, p.creditorAccount, p.remittanceInfo, p.endToEndId, " +
           "CAST(p.status AS string), p.connectionId, e.bankName, e.hostId, p.createdAt) " +
           "FROM Payment p JOIN EbicsConnection e ON p.connectionId = e.id " +
           "WHERE p.id = :paymentId")
    Optional<PaymentDetails> findPaymentDetails(@Param("paymentId") Long paymentId);
}
