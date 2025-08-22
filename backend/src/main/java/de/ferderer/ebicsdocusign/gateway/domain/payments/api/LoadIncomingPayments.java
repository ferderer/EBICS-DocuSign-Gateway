package de.ferderer.ebicsdocusign.gateway.domain.payments.api;

import de.ferderer.ebicsdocusign.gateway.domain.payments.model.IncomingPayment;
import de.ferderer.ebicsdocusign.gateway.domain.payments.model.PaymentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoadIncomingPayments {
    
    private final PaymentRepository repository;
    
    @GetMapping("/api/payments/incoming")
    public List<IncomingPayment> getIncomingPayments(
            @RequestParam(defaultValue = "50") int limit) {
        
        return repository.findRecentIncomingPayments(limit);
    }
}
