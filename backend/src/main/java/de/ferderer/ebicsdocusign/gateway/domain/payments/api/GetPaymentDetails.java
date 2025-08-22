package de.ferderer.ebicsdocusign.gateway.domain.payments.api;

import de.ferderer.ebicsdocusign.gateway.common.error.AppException;
import de.ferderer.ebicsdocusign.gateway.common.error.ErrorCode;
import de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentDetails;
import de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetPaymentDetails {
    private final PaymentRepository repository;

    @GetMapping("/api/payments/{paymentId}")
    public PaymentDetails getPaymentDetails(@PathVariable Long paymentId) {
        return repository.findPaymentDetails(paymentId)
            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND)
                .withData("paymentId", paymentId));
    }
}
